package com.techspark.core.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.techspark.core.model.Action
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionDao {

    @Insert
    suspend fun add(action:Action):Long

    @Query("SELECT * from `action` ORDER BY startDate DESC")
     fun getAll():Flow<List<Action>>

    /**
     * THe action can only start until the enddate
     */
    @Query("Select * from `action` where  startDate>= :startDate AND startDate < :endDate AND endDate <= :endDate ORDER BY startDate DESC")
     fun getAllInInterval(startDate:Long, endDate:Long):Flow<List<Action>>

    @Query("SELECT * from `action` WHERE id= :actionId")
    suspend fun getActionById(actionId:Long):Action?

    @Query("SELECT * from `action` WHERE startDate>= :startDate AND endDate == 0 ")
    suspend fun getOngoingAction(startDate: Long):Action?

    @Query("Select * from `action` where " +
            " startDate>= :startDate AND endDate <= :endDate " +
            " AND type= :type ORDER BY startDate DESC")
    fun getAllInIntervalWithType(startDate:Long, endDate:Long, type:Action.Type):Flow<List<Action>>

    @Update
    suspend fun update(action:Action)

    @Query("DELETE  FROM `action` WHERE id=:id")
    suspend fun delete(id:Long)

    @Query("SELECT COUNT(*) from `action`")
    suspend fun getTotalCount():Int
}