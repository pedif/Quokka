package com.techspark.core.data

import com.google.common.truth.Truth.assertThat
import com.techspark.core.common.DataHelper
import com.techspark.core.common.DateUtil
import com.techspark.core.test.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RepositoryTest {

    lateinit var repository: FakeRepository
    
    @Before
    fun setUp(){
        repository = FakeRepository()
    }
    @Test
    fun addAction_getSameAction(){
        val action = DataHelper.getFeelings(System.currentTimeMillis())[0]
        repository.addAction(action)
        val result = repository.getActionSync(1L)
        assertThat(result?.id).isEqualTo(1L)
    }

    @Test
    fun addTodayActions_returnsOneDay(){
        val date = System.currentTimeMillis()
        val actions = DataHelper.getFeelings(date)
        actions.forEach{
            repository.addAction(it)
        }
        val result = repository.getAllDaysSync(date)
        assertThat(result).hasSize(1)
    }

    @Test
    fun addTodayActions_returnsOneDayWithSameActions(){
        val date = System.currentTimeMillis()
        val actions = DataHelper.getFeelings(date)
        actions.forEach{
            repository.addAction(it)
        }
        val result = repository.getAllDaysSync(date)
        assertThat(result[0].actions).hasSize(5)
    }

    @Test
    fun noActionAdded_returnsOneEmptyDay(){
        val date = System.currentTimeMillis()
        val result = repository.getAllDaysSync(date)
        assertThat(result[0].actions).hasSize(0)
    }

    @Test
    fun addYesterdayActions_returnsEmptyDayOne(){
        val date = System.currentTimeMillis()
        val actions = DataHelper.getFeelings(DateUtil.getYesterdayDate(date))
        actions.forEach{
            repository.addAction(it)
        }
        val result = repository.getAllDaysSync(date)
        assertThat(result[0].actions).hasSize(0)
    }


    @Test
    fun addYesterdayActions_returnsYesterdayWithSameActions(){
        val date = System.currentTimeMillis()
        val actions = DataHelper.getFeelings(DateUtil.getYesterdayDate(date))
        actions.forEach{
            repository.addAction(it)
        }
        val result = repository.getAllDaysSync(date)
        assertThat(result[1].actions).hasSize(5)
    }

    @Test
    fun addMultipleDayWithMiddleEmptyDay_returnsMultipleDaysWithMiddleEmptyDay(){
        var date =System.currentTimeMillis()
        for( i in 0..2) {
            val actions = DataHelper.getFeelings(date)
            actions.forEach {
                repository.addAction(it)
            }
            date =DateUtil.getTomorrowDate(date)
        }

        date =DateUtil.getTomorrowDate(date)
        for( i in 0..2) {
            val actions = DataHelper.getFeelings(date)
            actions.forEach {
                repository.addAction(it)
            }
            date =DateUtil.getTomorrowDate(date)
        }

        val result = repository.getAllDaysSync(DateUtil.getYesterdayDate(date))
        assertThat(result[3].actions).hasSize(0)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addYesterdayActions_returnsYesterdayWithSameActions_inFlow() = runTest{
        val date = System.currentTimeMillis()
        val actions = DataHelper.getFeelings(DateUtil.getYesterdayDate(date))
        actions.forEach{
            repository.addAction(it)
        }
        val result = repository.getAllDays(date).first().data!!
        assertThat(result[0].actions).hasSize(5)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun emptyDatabase_returnsOneEmptyDay() = runTest{

        val date = System.currentTimeMillis()
        val result = repository.getAllDays(date).first().data!!
        assertThat(result[0].startDate).isEqualTo(date)
    }

}