package com.techspark.core.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.techspark.core.data.db.ActionDao
import com.techspark.core.data.db.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import com.google.common.truth.Truth.assertThat
import com.techspark.core.common.DateUtil
import com.techspark.core.model.Action
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first

import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IFeelRepositoryTest {

    private lateinit var actionDao: ActionDao
    private lateinit var db: AppDatabase
    private lateinit var repo:IFeelRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        actionDao = db.actionDao()
        repo = IFeelRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }


    @Test
    fun gettingPassedDayAction_WhileHavingOngoingAction_shouldReturnEmpty() = runTest{

        val dayId = DateUtil.getStartOfDay(System.currentTimeMillis())
        val tomorrow = DateUtil.getTomorrowDate(dayId)
        val item =Action(0,"",tomorrow+2, 0L, Action.Type.HAPPINESS, "")
        repo.addAction(item)
        val day = repo.getDay(dayId).drop(1).first().data

        assertThat(day?.actions).hasSize(0)
    }

    @Test
    fun gettingTodayAction_WhileHavingOngoingAction_ShouldReturnOneAction() = runTest{

        val dayId = DateUtil.getStartOfDay(System.currentTimeMillis())
        val item =Action(0,"",dayId+2, 0L, Action.Type.HAPPINESS, "")
        repo.addAction(item)
        val day = repo.getDay(dayId).drop(1).first().data

        assertThat(day?.actions).hasSize(1)
    }
}