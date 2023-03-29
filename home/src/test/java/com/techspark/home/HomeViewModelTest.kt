package com.techspark.home

import com.techspark.core.test.FakeRepository
import com.techspark.core.test.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.techspark.core.common.DateUtil
import com.techspark.core.model.Action
import com.techspark.core.test.DateHelper
import java.util.Date
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest{

    private lateinit var viewModel: HomeViewModel
    private lateinit var repo: FakeRepository

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    fun setUp(){
         repo = FakeRepository()
        viewModel = HomeViewModel(repo)
    }

    @Test
    fun emptyDatabase_returns_No_Data() = runTest{

        viewModel.getActions()
        val state = viewModel.state
        assertThat(state.yesterdayLargestAction).isEqualTo(Action.Type.NO_INPUT)
    }

    @Test
    fun ongoingAction_results_newActions_forPreviousDays() = runTest{
        val endDate = DateUtil.getStartOfDay(System.currentTimeMillis())
        val startDate = endDate - TimeUnit.DAYS.toMillis(4)
         val act = Action(1L, "",startDate, 0L,Action.Type.ANGER,"" )
        repo.addAction(act)
        viewModel.getActions()
        viewModel.getActions()
        val state = viewModel.state
        assertThat(state.dayList).hasSize(5)
    }

    @Test
    fun most_felt_depression_last_week_return_depression_as_most_felt() = runTest{
        val startDate =DateUtil.getStartOfDay(System.currentTimeMillis())
        val prevDate = DateHelper.getDateByDay(startDate, -2)
        val oneHourDuration= DateHelper.getDateByHour(prevDate, 1)
        val threeHourDuration = DateHelper.getDateByHour(prevDate, 3)
        val act = Action(1L, "",prevDate,
            oneHourDuration,Action.Type.HAPPINESS,"" )
        val act2 = Action(1L, "",prevDate,
            threeHourDuration, Action.Type.DEPRESSED,"" )
        repo.addAction(act)
        repo.addAction(act2)
        viewModel.getActions()
        val state = viewModel.state
        assertThat(state.yesterdayLargestAction).isEqualTo(Action.Type.NO_INPUT)
    }
}