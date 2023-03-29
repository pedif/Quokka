package com.techspark.home

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.techspark.core.common.DataHelper
import com.techspark.core.test.FakeRepository
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class HomeTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun noPastData_NoChartShown(){
        rule.setContent { HomeScreen(HomeViewModel(FakeRepository())) }

        rule.onNodeWithContentDescription("chart").assertDoesNotExist()
    }

    @Test
    fun pastData_ChartShown(){

        val repo = FakeRepository()
        val items = DataHelper.getFeelings(System.currentTimeMillis()-TimeUnit.DAYS.toMillis(2))
        items.forEach {
            repo.addAction(it)
        }
        rule.setContent {
            HomeScreen(HomeViewModel(repo))
        }

        rule.onNodeWithContentDescription("chart").assertExists()
    }
}