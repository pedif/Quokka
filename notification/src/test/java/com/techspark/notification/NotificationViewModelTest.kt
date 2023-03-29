package com.techspark.notification

import com.techspark.core.test.FakeRepository
import com.techspark.core.test.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

import com.google.common.truth.Truth.assertThat
import com.techspark.core.model.Action
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    private lateinit var viewModel: NotificationViewModel

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Test
    fun No_Ongoing_ShowsNoOngoing() = runTest {

        val repo = FakeRepository()
        viewModel = NotificationViewModel(repo)

        viewModel.getOngoingAction()

        assertThat(viewModel.state.isOngoing).isFalse()


    }

    @Test
    fun Ongoing_ShowsOngoing() = runTest {

        val repo = FakeRepository()
        viewModel = NotificationViewModel(repo)
        repo.addAction(Action(1, "", System.currentTimeMillis(), 0L, Action.Type.NO_INPUT, ""))

        viewModel.getOngoingAction()

        assertThat(viewModel.state.isOngoing).isTrue()
    }


    @Test
    fun add_Action_when_NoOngoing_AddsOneAction() = runTest {
        val repo = FakeRepository()
        viewModel = NotificationViewModel(repo)
        viewModel.getOngoingAction()
        viewModel.saveAction()
        assertThat(repo.items).hasSize(1)

    }

    @Test
    fun add_OngoingAction_when_OngoingForToday_AddsOneAction() = runTest {
        val repo = FakeRepository()
        viewModel = NotificationViewModel(repo)
        repo.addAction(Action(1, "",
            System.currentTimeMillis(), 0L, Action.Type.NO_INPUT, ""))
        viewModel.getOngoingAction()
        viewModel.saveAction()
        assertThat(repo.items).hasSize(1)
    }

    @Test
    fun add_OngoingAction_Ends_OngoingAction() = runTest {
        val repo = FakeRepository()
        viewModel = NotificationViewModel(repo)
        repo.addAction(Action(1, "",
            System.currentTimeMillis(), 0L, Action.Type.NO_INPUT, ""))
        viewModel.getOngoingAction()
        viewModel.saveAction()
        viewModel.getOngoingAction()
        assertThat(viewModel.state.isOngoing).isFalse()
    }

    @Test
    fun add_OnGoingFor3DaysAction_Adds_ThreeActions()=  runTest {
        val repo = FakeRepository()
        viewModel = NotificationViewModel(repo)
        //3 days means today, yesterday and the day before
        repo.addAction(Action(1, "",
            System.currentTimeMillis()-TimeUnit.DAYS.toMillis(2), 0L, Action.Type.NO_INPUT, ""))
        viewModel.getOngoingAction()
        viewModel.saveAction()
        viewModel.getOngoingAction()
        assertThat(repo.items).hasSize(3)
    }

    @Test
    fun add_Action_when_NoOngoing_AddsOneOngoing() = runTest {
        val repo = FakeRepository()
        viewModel = NotificationViewModel(repo)
        viewModel.getOngoingAction()
        viewModel.saveAction()
        viewModel.getOngoingAction()
        assertThat(viewModel.state.isOngoing).isTrue()

    }
}