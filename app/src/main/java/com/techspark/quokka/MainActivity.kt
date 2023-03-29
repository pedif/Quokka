package com.techspark.quokka


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.techspark.core.common.DateUtil
import com.techspark.core.data.Repository
import com.techspark.core.data.pref.IFeelPref
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.theme.PremiumColor
import com.techspark.core.theme.ToolbarColor
import com.techspark.notification.util.NotificationUtil
import com.techspark.onboarding.OnBoardingScreen
import com.techspark.quokka.model.MainState
import com.techspark.quokka.navigation.HomeDestination
import com.techspark.quokka.navigation.IFeelNavHost
import com.techspark.quokka.navigation.StatisticsDestination
import com.techspark.quokka.navigation.navigateSingleTopTo
import com.techspark.revenue.RevenueScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.techspark.home.RateUsDialog


enum class DIALOG {
    NONE, SUPPORT, ONBOARDING, RATE
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: Repository;

    var state by mutableStateOf(MainState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        setContent {
            IFeelTheme {
                MainScreen(state,
                    onSupportClicked = { state = state.copy(dialogState = DIALOG.SUPPORT) },
                    onOnBoardingClicked = { state = state.copy(dialogState = DIALOG.ONBOARDING) },
                    onShouldShowRateDialog = { state = state.copy(dialogState = DIALOG.RATE) },
                    onDialogDismissed = { state = state.copy(dialogState = DIALOG.NONE) })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        NotificationUtil.removeNotification(this)
        lifecycleScope.launch {
            this@MainActivity.let { context ->
                val isFirstTime = IFeelPref.isFirstTime(context)
                val isSubscribed = IFeelPref.isSubscribed(context)
                state = if (isFirstTime) {
                    IFeelPref.updateFirstTimeStatus(context, false)
                    state.copy(
                        dialogState = DIALOG.ONBOARDING, isSubscribed = isSubscribed
                    )
                } else
                    state.copy(isSubscribed = isSubscribed)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.Main).launch {
            val ongoing = repository.getOngoingAction(DateUtil.getStartOfToday()).data
            NotificationUtil.showOngoingNotification(this@MainActivity, ongoing)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainState,
    onSupportClicked: () -> Unit = {},
    onOnBoardingClicked: () -> Unit = {},
    onShouldShowRateDialog: () -> Unit = {},
    onDialogDismissed: () -> Unit = {}
) {
    val navController = rememberNavController()
    MainScreenWithNav(
        state = state, navController = navController,
        onSupportClicked = onSupportClicked,
        onOnBoardingClicked = onOnBoardingClicked,
        onShouldShowRateDialog = onShouldShowRateDialog,
        onDialogDismissed = onDialogDismissed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithNav(
    state: MainState,
    navController: NavHostController,
    onSupportClicked: () -> Unit = {},
    onOnBoardingClicked: () -> Unit = {},
    onShouldShowRateDialog: () -> Unit = {},
    onDialogDismissed: () -> Unit = {}
) {
    val currentBackStack by navController.currentBackStackEntryAsState()


    var showFab by rememberSaveable {
        mutableStateOf(false)
    }
    var showActionSheet by rememberSaveable {
        mutableStateOf(false)
    }
    var showToolbar by rememberSaveable {
        mutableStateOf(true)
    }
    var shouldShowBackButton by remember(currentBackStack) {
        mutableStateOf(currentBackStack?.destination?.route != HomeDestination.route)
    }


    Scaffold(topBar = {
        IFeelTopBar(modifier = Modifier,
            showActionSheet = showActionSheet,
            shouldShowBackButton = shouldShowBackButton,
            isSubscribed = state.isSubscribed,
            onStatisticsClicked = {
                if (!showActionSheet) navController.navigateSingleTopTo(
                    StatisticsDestination.route
                )
            },
            onSupportClicked = onSupportClicked,
            onOnBoardingClicked = onOnBoardingClicked,
            onBackButtonClicked = {
                if (!showActionSheet) navController.navigateSingleTopTo(
                    HomeDestination.route, false
                )
            })
    }, floatingActionButton = {
        if (!showActionSheet && showFab) FloatingActionButton(
//            modifier = Modifier.semantics {
//                contentDescription = "Add Feeling"
//            }
            onClick = {
                showActionSheet = true
            }) {
            Icon(
                Icons.Filled.Add, "Add Feeling", modifier = Modifier.clip(CircleShape)
            )
        }
    }) {
        IFeelNavHost(navController = navController,
            modifier = Modifier.padding(it),
            showActionSheet = showActionSheet,
            shouldShowToolBar = { shouldShow -> showToolbar = shouldShow },
            shouldShowFab = { shouldShow -> showFab = shouldShow },
            onSheetDismissed = {
                showActionSheet = false
            },
            onShowRateDialog = onShouldShowRateDialog,
            onActionItemSelected = { showActionSheet = true })

        when (state.dialogState) {
            DIALOG.SUPPORT -> {
                RevenueScreen(onDismiss = onDialogDismissed)
            }
            DIALOG.ONBOARDING -> {
                OnBoardingScreen(onDismiss = onDialogDismissed)
            }
            DIALOG.RATE -> {
                RateUsDialog(onDismiss = onDialogDismissed)
            }
            else -> {}
        }
    }
}

@Composable
fun Greeting(name: String) {
    Column {

        Text(text = "Hello $name!")
        Image(imageVector = Icons.Default.Add, contentDescription = null)
    }
}


private val TabHeight = 56.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IFeelTopBar(
    modifier: Modifier = Modifier,
    showActionSheet: Boolean = false,
    shouldShowBackButton: Boolean = true,
    isSubscribed: Boolean = false,
    onStatisticsClicked: () -> Unit = {},
    onSupportClicked: () -> Unit = {},
    onOnBoardingClicked: () -> Unit = {},
    onBackButtonClicked: () -> Unit = {}
) {

    val color = if (showActionSheet) ToolbarColor
    else MaterialTheme.colorScheme.surfaceVariant

    val suppColor = if (isSubscribed) PremiumColor
    else MaterialTheme.colorScheme.onSurfaceVariant
    TopAppBar(title = {
        Text("Quokka")
    },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = color),
        navigationIcon = {
            if (shouldShowBackButton) IconButton(onClick = onBackButtonClicked) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        },
        actions = {
            IconButton(
                onClick = onStatisticsClicked, modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp)
            ) {
                Icon(painterResource(id = R.drawable.ic_bar_chart), null)
            }

            IconButton(
                onClick = onSupportClicked, modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_favorite),
                    tint = suppColor,
                    contentDescription = null
                )
            }


            IconButton(
                onClick = onOnBoardingClicked, modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp)
            ) {
                Icon(
                    Icons.Filled.Info, tint = suppColor, contentDescription = null
                )
            }
        })
//    Surface(
//        Modifier
//            .fillMaxWidth()
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.End
//        ) {
//
//            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Subscribe",
//                modifier = Modifier
//                    .size(TabHeight, TabHeight)
//                    .padding(8.dp)
//                    .clickable { onSupportClicked() })
//
//            Icon(imageVector = Icons.Filled.List, contentDescription = "Statistics",
//                modifier = Modifier
//                    .size(TabHeight, TabHeight)
//                    .padding(8.dp)
//                    .clickable { onStatisticsClicked() })
//
//        }
//    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IFeelTheme {
        MainScreen(MainState())
    }
}

