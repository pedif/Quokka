package com.techspark.core.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object IFeelPref {
    // At the top level of your kotlin file:
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val subscriptionKey = booleanPreferencesKey("is_subscribed")
    private val firstTimeKey = booleanPreferencesKey("is_first_time")
    private val lastActionCountKey = intPreferencesKey("last_action_count")
    private val hasRatedKey = booleanPreferencesKey("has_rated")

    suspend fun updateSubscriptionStatus(context: Context, isSubscribed: Boolean) {
        context.dataStore.edit { settings ->
            settings[subscriptionKey] = isSubscribed
        }
    }

    suspend fun isSubscribed(context: Context): Boolean {

        val pref = context.dataStore.data.first()
        return pref[subscriptionKey] ?: false
    }

    suspend fun updateFirstTimeStatus(context: Context, isFirstTime: Boolean) {
        context.dataStore.edit { settings ->
            settings[firstTimeKey] = isFirstTime
        }
    }

    suspend fun isFirstTime(context: Context):Boolean{
        val pref = context.dataStore.data.first()
        return pref[firstTimeKey] ?: true
    }

    suspend fun updateLastRateDate(context: Context, date:Int){
        context.dataStore.edit { settings ->
            settings[lastActionCountKey] = date
        }
    }

    suspend fun getLastRateDate(context: Context):Int{
        val pref = context.dataStore.data.first()
        return pref[lastActionCountKey] ?: 0
    }

    suspend fun updateRateStatus(context: Context, hasRated: Boolean) {
        context.dataStore.edit { settings ->
            settings[hasRatedKey] = hasRated
        }
    }

    suspend fun hasRated(context: Context): Boolean {

        val pref = context.dataStore.data.first()
        return pref[hasRatedKey] ?: false
    }

}