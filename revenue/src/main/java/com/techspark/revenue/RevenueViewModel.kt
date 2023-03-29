package com.techspark.revenue

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.techspark.core.data.pref.IFeelPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

val AD_UNIT_ID = if (BuildConfig.DEBUG) BuildConfig.Debug_AD_ID else BuildConfig.Release_AD_ID
const val SUB_ID = "support_monthly"

@HiltViewModel
class RevenueViewModel @Inject constructor(
) : ViewModel() {


    private var _state by mutableStateOf(RevenueState())

    val state: State<RevenueState>
        get() = derivedStateOf { _state }

    /**
     * We get the subscription status on this screen,
     * if the user has started the subscription phase
     * we should show a message to them in case they've purchased a sub
     * otherwise, we dont show any mesage
     */
    var userStartedAction = false


    override fun onCleared() {
        super.onCleared()
        if (this::billingClient.isInitialized && billingClient.isReady)
            billingClient.endConnection()
    }


    fun getSubscriptionStatus(context: Context) {
        viewModelScope.launch {
            _state = _state.copy(
                isSubscribed = IFeelPref.isSubscribed(context)
            )
        }
    }

    fun onMessageShown() {

        userStartedAction = false
    }

    lateinit var billingClient: BillingClient
    fun initBillingClient(context: Context) {
        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            if ((billingResult.responseCode == BillingClient.BillingResponseCode.OK ||
                        billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) && purchases != null
            ) {
                var isSubscribed = false
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        //We need to notify the user if they have started this action
                        //because this callback works also on initialization
                        var toastCode = ErrorCode.NONE
                        if (userStartedAction)
                            toastCode = ErrorCode.Successful;

                        _state = _state.copy(
                            isSubscribed = true,
                            code = toastCode
                        )
                        isSubscribed = true
                    }
                }
                viewModelScope.launch {
                    IFeelPref.updateSubscriptionStatus(context, isSubscribed)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                _state = _state.copy(
                    isSubscribed = false,
                    code = ErrorCode.USER_CANCELED
                )
                viewModelScope.launch {
                    IFeelPref.updateSubscriptionStatus(context, false)
                }
            } else {
                // Handle any other error codes.
                _state = _state.copy(code = ErrorCode.UNKNOWN)
            }
        }

        billingClient = BillingClient.newBuilder(context).setListener(purchasesUpdatedListener)
            .enablePendingPurchases().build()
        connectBillingClient(context)
    }

    private fun connectBillingClient(context: Context) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    getProducts()
                    updateSubStatus(context)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun updateSubStatus(context: Context) {
        viewModelScope.launch {

            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)

// uses queryPurchasesAsync Kotlin extension function
            val purchasesResult = billingClient.queryPurchasesAsync(params.build())

// check purchasesResult.billingResult
// process returned purchasesResult.purchasesList, e.g. display the plans user owns
            if (purchasesResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchasesResult.purchasesList.isEmpty())
                    IFeelPref.updateSubscriptionStatus(context, false)
                purchasesResult.purchasesList.forEach {
                    Timber.e("${it.packageName} -  ${it.originalJson}")
                    (it.products.find { p -> p.equals(SUB_ID) } != null)
                    IFeelPref.updateSubscriptionStatus(context, true)
                }
                getSubscriptionStatus(context)
            }
        }
    }

    private var subProduct: ProductDetails? = null
    private fun getProducts() {
        val productList = ArrayList<Product>()
        productList.add(
            Product.newBuilder().setProductId(SUB_ID).setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        if (billingClient.isFeatureSupported(BillingClient.FeatureType.PRODUCT_DETAILS).responseCode != BillingClient.BillingResponseCode.OK
        ) {
            _state = _state.copy(code = ErrorCode.PLAY_SERVICES)
            return
        }

        // leverage queryProductDetails Kotlin extension function
        viewModelScope.launch {
            val productDetailsResult = billingClient.queryProductDetails(params.build())
            productDetailsResult.productDetailsList?.forEach {
                subProduct = it
                Timber.e(it.name + ":" + it.description + "," + it.title + "::" + it.subscriptionOfferDetails?.size)
            }
            // Process the result.
        }

    }

    fun purchaseSub(activity: Activity) {
        userStartedAction = true
        if (subProduct == null || subProduct?.subscriptionOfferDetails?.size == 0) {
            _state = _state.copy(code = ErrorCode.NO_PRODUCT)
            return
        }
        val offerToken = subProduct?.subscriptionOfferDetails?.get(0)?.offerToken ?: return
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                .setProductDetails(subProduct!!)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

// Launch the billing flow
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            _state = _state.copy(code = ErrorCode.UNKNOWN)
        }
    }

    var autoPlay: Boolean = false

    /**
     * PreLoad a rewarded ad to be displayed to the user
     * @param autoPlay whether this ad should instantly be displayed after loading
     */
    private fun loadRewardedAd(context: Activity) {
        if (rewardedAd == null && !isLoading) {
            isLoading = true
//            var adRequest = AdRequest.Builder().build()
            val adRequest = AdManagerAdRequest.Builder().build()
//            val extras = Bundle()
//            if(!allAds)
//            extras.putString("npa", "1")
//            val adRequest = AdManagerAdRequest.Builder()
//                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
//                .build()
            RewardedAd.load(context, AD_UNIT_ID, adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.e(adError?.message)
                    isLoading = false
                    rewardedAd = null
                    _state = _state.copy(code = ErrorCode.AD_NOT_LOADED)
                    logAdData(hasLoaded = !autoPlay, successful = false)
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Timber.e("Ad was loaded.")
                    rewardedAd = ad
                    isLoading = false
                    if (autoPlay)
                        showRewardedVideo(context)
                    logAdData(hasLoaded = !autoPlay, successful = true)
                    autoPlay = false
                }
            })
        }
    }

    private var rewardedAd: RewardedAd? = null
    var isLoading = false


    fun showRewardedVideo(context: Activity) {
        if (rewardedAd != null) {
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Timber.d("Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    rewardedAd = null
                    loadRewardedAd(context)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Timber.d("Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    rewardedAd = null
                    loadRewardedAd(context)
                }

                override fun onAdShowedFullScreenContent() {
                    Timber.d("Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }

            rewardedAd?.show(context) { rewardItem ->
//                    var rewardAmount = rewardItem.amount
//                        addCoins(rewardAmount)
                Timber.d("User earned the reward.")
                userStartedAction = true;
                autoPlay = false
                _state = _state.copy(code = ErrorCode.Successful)
                loadRewardedAd(context)
            }
        } else {
            autoPlay = true
            loadRewardedAd(context)
            Timber.e("Ad not loaded trying again")
            logAdData(
                hasLoaded = !autoPlay,
                successful = false,
                comment = "Ad was not loaded when user tried to watch it"
            )
        }
    }


    private fun logAdData(hasLoaded: Boolean, successful: Boolean, comment: String = "") {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, comment)
            putString("successful", successful.toString())
            putString("was_already_loaded", hasLoaded.toString())
        }
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params)
    }

    private lateinit var consentInformation: ConsentInformation
    private var consentForm: ConsentForm? = null
    private var allAds = false

    fun checkConsentStatus(context: Activity) {
        // Set tag for underage of consent. Here false means users are not underage.
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

//         val debugSettings = ConsentDebugSettings.Builder(context)
//             .setDebugGeography(ConsentDebugSettings
//                 .DebugGeography
//                 .DEBUG_GEOGRAPHY_EEA)
//             .addTestDeviceHashedId("E622382D01CE0BD8ECA76E51FC82E4E4")
//             .build()

//         val params = ConsentRequestParameters
//             .Builder()
//             .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(context)
//         consentInformation.reset()
        consentInformation.requestConsentInfoUpdate(
            context,
            params,
            {
                // The consent information state was updated.
                // You are now ready to check if a form is available.
                if (consentInformation.isConsentFormAvailable) {
                    loadForm(context)
                } else {
                    allAds = true
                    loadRewardedAd(context)
                }
            },
            { formError ->
                // Handle the error.
                loadRewardedAd(context)
            }
        )
    }

    private fun loadForm(context: Activity) {
        UserMessagingPlatform.loadConsentForm(
            context,
            { consentForm ->
                Timber.e("${consentInformation.consentStatus}")
                this.consentForm = consentForm

                if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    consentForm.show(context) { formError ->
                        // Handle dismissal by reloading form.
                        loadForm(context)
                    }
                } else {
                    loadRewardedAd(context)
                }
            },
            { formError ->
                // Handle the error.
            }
        )
    }


}