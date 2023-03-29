import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.techspark.core.common.DateUtil
import com.techspark.core.data.db.ActionDao
import com.techspark.core.data.db.AppDatabase
import com.techspark.core.model.Action
import com.techspark.core.model.FakeDataHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var actionDao: ActionDao
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        actionDao = db.actionDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAction_ReturnsAction() = runTest {
        val action = Action(
            0, "action", System.currentTimeMillis(),
            System.currentTimeMillis() + 3610L, Action.Type.HAPPINESS, ""
        )
        actionDao.add(action)
        val items = actionDao.getAll().first()
        assertThat(items).hasSize(1)
    }

    @Test
    fun addYesterdayActions_returnsNoActionsForToday() = runTest {

        val date = System.currentTimeMillis()
        val yesterday = DateUtil.getYesterdayDate(date)
        val items = FakeDataHelper.getFeelings(yesterday)
        items.forEach {
            actionDao.add(it)
        }
        val result = actionDao.getAllInInterval(yesterday, date).first()
        assertThat(result).hasSize(5)
    }



}