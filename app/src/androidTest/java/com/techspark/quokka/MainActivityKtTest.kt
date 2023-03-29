package com.techspark.quokka

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.techspark.home.model.HomeState
import com.techspark.quokka.di.DatabaseModule
import com.techspark.quokka.model.MainState
import com.techspark.quokka.navigation.IFeelNavHost
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainActivityKtTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val rule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setupNavHost() {
        hiltRule.inject()

    }

    @Test
    fun navHost_verifyDayScreen_addFeeling_shows() {

        rule.onNodeWithContentDescription("today").performClick()
        rule.onNodeWithContentDescription("Add Feeling").performClick()
        rule.onNodeWithContentDescription("add action screen").assertIsDisplayed()
        with(rule) {
            onNodeWithText(activity.getString(com.techspark.day.R.string.save)).performClick()
//            Thread.sleep(5_000)
            onNodeWithTag("tag").assertIsDisplayed()
        }
    }
}