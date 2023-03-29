package com.techspark.core.model

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeelingTest{
    private lateinit var context: Context
        @Before
    fun setUp(){
            // Context of the app under test.
        context= InstrumentationRegistry.getInstrumentation().targetContext

    }
    @Test
    fun itemLabel_NotEmpty(){

        val item = FakeDataHelper.getFeelings(System.currentTimeMillis())[0]
        assertThat(item.getDurationLabel(context)).isNotEmpty()
    }
}