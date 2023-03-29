package com.techspark.day.day

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.techspark.core.common.DataHelper
import com.techspark.core.common.DateUtil
import com.techspark.core.model.Action
import com.techspark.core.test.FakeRepository
import com.techspark.core.test.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalCoroutinesApi::class)
class DayViewModelTest {

    private lateinit var viewModel: DayViewModel

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    fun setUp() = runTest {
        val repo = FakeRepository()
        DataHelper.getFeelings(System.currentTimeMillis()).forEach { repo.addAction(it) }
        viewModel = DayViewModel(repo, SavedStateHandle())
    }


    @Test
    fun zeroId_returnsAction() = runTest {
        viewModel.getAction()
        val state = viewModel.actionState
        assertThat(state.action.id).isEqualTo(0L)
    }

    @Test
    fun validId_returnsAction() = runTest {
        viewModel.actionId = 1L
        viewModel.getAction()
        val state = viewModel.actionState
        assertThat(state.action.id).isEqualTo(1L)
    }


    @Test
    fun today_returns_Actions() = runTest {
        val repo = FakeRepository()
        DataHelper.getFeelings(System.currentTimeMillis()).forEach { repo.addAction(it) }
        val handler = HashMap<String, Any>()
        handler[viewModel.dayIdKey] = DateUtil.getStartOfDay(System.currentTimeMillis())
        viewModel = DayViewModel(repo, SavedStateHandle(handler))
        val state = viewModel.state
        assertThat(state.day.actions).hasSize(5)
    }

    @Test
    fun add_action_updates_dayState() = runTest {
        val repo = FakeRepository()
        DataHelper.getFeelings(System.currentTimeMillis()).forEach { repo.addAction(it) }
        val handler = HashMap<String, Any>()
        handler[viewModel.dayIdKey] = DateUtil.getStartOfDay(System.currentTimeMillis())
        viewModel = DayViewModel(repo, SavedStateHandle(handler))
        viewModel.updateAction(
            Action(
                0L, "6", System
                    .currentTimeMillis(), System.currentTimeMillis() + TimeUnit.HOURS.toMillis(3),
                Action.Type.HAPPINESS, ""
            )
        )
        viewModel.saveAction(true)
        viewModel.getDay()
        val state = viewModel.state
        assertThat(state.day.actions).hasSize(6)
    }

    @Test
    fun add_noEndAction_Returns_OngoingAction() = runTest {
        val repo = FakeRepository()
        DataHelper.getFeelings(System.currentTimeMillis()).forEach { repo.addAction(it) }
        val handler = HashMap<String, Any>()
        handler[viewModel.dayIdKey] = DateUtil.getStartOfDay(System.currentTimeMillis())
        viewModel = DayViewModel(repo, SavedStateHandle(handler))
        viewModel.updateAction(
            Action(
                0L, "6", System
                    .currentTimeMillis(), 0L,
                Action.Type.HAPPINESS, ""
            )
        )
        viewModel.saveAction(true)
        viewModel.getDay()

        assertThat(viewModel.ongoingAction?.id).isEqualTo(6)
    }

    @Test
    fun saveNewAction_withOnGoing_warns() = runTest {
        val repo = FakeRepository()
        DataHelper.getFeelings(System.currentTimeMillis()).forEach { repo.addAction(it) }
        val handler = HashMap<String, Any>()
        handler[viewModel.dayIdKey] = DateUtil.getStartOfDay(System.currentTimeMillis())
        viewModel = DayViewModel(repo, SavedStateHandle(handler))
        viewModel.updateAction(
            Action(
                0L, "6", System
                    .currentTimeMillis(), 0L,
                Action.Type.HAPPINESS, ""
            )
        )
        viewModel.saveAction(true)
        viewModel.getDay()
        viewModel.updateAction(
            Action(
                0L, "6", returnPlusHour(1), returnPlusHour(3),
                Action.Type.HAPPINESS, ""
            )
        )
        viewModel.saveAction(true)
        viewModel.getDay()

        assertThat(viewModel.ongoingAction).isNull()
    }

    @Test
    fun saveNewAction_withOnGoing_ends() = runTest {
        val repo = FakeRepository()
        DataHelper.getFeelings(System.currentTimeMillis()).forEach { repo.addAction(it) }
        val handler = HashMap<String, Any>()
        handler[viewModel.dayIdKey] = DateUtil.getStartOfDay(System.currentTimeMillis())
        viewModel = DayViewModel(repo, SavedStateHandle(handler))
        viewModel.updateAction(
            Action(
                0L, "6", System
                    .currentTimeMillis(), 0L,
                Action.Type.HAPPINESS, "ongoing"
            )
        )
        viewModel.saveAction(true)
        viewModel.getDay()
        val target = returnPlusHour(1)
        viewModel.updateAction(
            Action(
                0L, "6", target, returnPlusHour(3),
                Action.Type.HAPPINESS, ""
            )
        )
        viewModel.saveAction(true)
        viewModel.getDay()
        val actions = viewModel.state.day.actions
        assertThat(actions.find { it.comment == "ongoing" }?.endDate).isEqualTo(target - 1)
    }

    @Test
    fun saveWithNoEndOngoing_warns() = runTest {
        val repo = FakeRepository()
        DataHelper.getFeelings(System.currentTimeMillis()).forEach { repo.addAction(it) }
        val handler = HashMap<String, Any>()
        handler[viewModel.dayIdKey] = DateUtil.getStartOfDay(System.currentTimeMillis())
        viewModel = DayViewModel(repo, SavedStateHandle(handler))
        viewModel.updateAction(
            Action(
                0L, "6", System
                    .currentTimeMillis(), 0L,
                Action.Type.HAPPINESS, "ongoing"
            )
        )
        viewModel.saveAction(true)
        viewModel.getDay()
        val target = returnPlusHour(1)
        viewModel.updateAction(
            Action(
                0L, "6", target, returnPlusHour(3),
                Action.Type.HAPPINESS, ""
            )
        )
        viewModel.saveAction(false)
        val state = viewModel.actionState
        assertThat(state.showConfirmationDialog).isEqualTo(true)
    }

    @Test
    fun saveNewAction_Makes_OnGoingNull() = runTest {
        val repo = FakeRepository()
        DataHelper.getFeelings(System.currentTimeMillis()).forEach { repo.addAction(it) }
        val handler = HashMap<String, Any>()
        handler[viewModel.dayIdKey] = DateUtil.getStartOfDay(System.currentTimeMillis())
        viewModel = DayViewModel(repo, SavedStateHandle(handler))
        viewModel.updateAction(
            Action(
                0L, "6", System
                    .currentTimeMillis(), 0L,
                Action.Type.HAPPINESS, "ongoing"
            )
        )
        viewModel.saveAction(true)
        viewModel.getDay()
        val target = returnPlusHour(1)
        viewModel.updateAction(
            Action(
                0L, "6", target, returnPlusHour(3),
                Action.Type.HAPPINESS, ""
            )
        )
        viewModel.saveAction(true)
        viewModel.getDay()
        assertThat(viewModel.ongoingAction).isNull()
    }

    @Test
    fun setActionStartTimeBeforeOngoing_will_ignore() = runTest {
        val repo = FakeRepository()
        DataHelper.getFeelings(System.currentTimeMillis()).forEach { repo.addAction(it) }
        val handler = HashMap<String, Any>()
        handler[viewModel.dayIdKey] = DateUtil.getStartOfDay(System.currentTimeMillis())
        viewModel = DayViewModel(repo, SavedStateHandle(handler))

        val target = returnPlusHour(1)
        viewModel.updateAction(
            Action(
                0L, "6",target , 0L,
                Action.Type.HAPPINESS, "ongoing"
            )
        )
        viewModel.saveAction(true)
        viewModel.getDay()
        viewModel.updateAction(
            Action(
                0L, "6", target-100, returnPlusHour(3),
                Action.Type.HAPPINESS, ""
            )
        )
        val state = viewModel.actionState
        assertThat(state.action.startDate).isEqualTo(target)
    }

    @Test
    fun add_action_finishes_task() = runTest {
        val repo = FakeRepository()
        DataHelper.getFeelings(System.currentTimeMillis()).forEach { repo.addAction(it) }
        val handler = HashMap<String, Any>()
        handler[viewModel.dayIdKey] = DateUtil.getStartOfDay(System.currentTimeMillis())
        viewModel = DayViewModel(repo, SavedStateHandle(handler))
        viewModel.updateAction(
            Action(
                0L, "6", System
                    .currentTimeMillis(), System.currentTimeMillis() + TimeUnit.HOURS.toMillis(3),
                Action.Type.HAPPINESS, ""
            )
        )
        viewModel.saveAction(true)
        val state = viewModel.actionState
        assertThat(state.hasFinishedTask).isTrue()
    }

    fun returnPlusHour(hour: Int, date: Long = System.currentTimeMillis()): Long {
        return date + TimeUnit.HOURS.toMillis(hour.toLong());
    }
}