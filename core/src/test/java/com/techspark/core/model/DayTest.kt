package com.techspark.core.model

import com.google.common.truth.Truth.assertThat
import com.techspark.core.common.DateUtil
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class DayTest {

    private lateinit var day: Day

    @Before
    fun setUp() {
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        val items = FakeDataHelper.getFeelings(cal.timeInMillis)
        day = Day(System.currentTimeMillis(), items)
    }

    @Test
    fun dayFeelingDurations_AreNotEmpty() {
        assertThat(day.feelingDuration.values.toList().filter { value -> value > 0 }).hasSize(6)
    }

    @Test
    fun feelingPercentage_isLessThanHundredPercent() {
        val percent = day.feelingDuration.values.sumOf { value -> DateUtil.getPercentagePerDay(value)}
        assertThat(percent).isLessThan(100)

    }

    @Test
    fun getMostFelt_returns_depression(){
        val feeling = day.getTheMostFeltFeelingType()
        assertThat(feeling).isEqualTo(Action.Type.DEPRESSED)
    }
}
