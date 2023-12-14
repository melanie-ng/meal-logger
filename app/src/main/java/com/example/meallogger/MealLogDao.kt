package com.example.meallogger

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealLogDao {
    @Query("SELECT * FROM meal_log_table")
    fun getAll(): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_log_table WHERE date = :date ORDER BY time")
    fun getByDate(date: String): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: MealLog)

    @Update
    suspend fun update(log: MealLog)

    @Delete
    suspend fun delete(log: MealLog)

    @Query("DELETE FROM meal_log_table")
    suspend fun deleteAll()
}