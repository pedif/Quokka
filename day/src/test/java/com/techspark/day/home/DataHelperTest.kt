package com.techspark.day.home

import com.google.common.truth.Truth.assertThat
import com.techspark.core.common.DataHelper
import org.junit.Test

class DataHelperTest {

    @Test
    fun getActions_IsNotEmpty() {

        val items = DataHelper.getFeelings(System.currentTimeMillis())

        assertThat(items).isNotEmpty()
    }

}