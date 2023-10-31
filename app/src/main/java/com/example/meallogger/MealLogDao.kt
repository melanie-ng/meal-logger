package com.example.meallogger

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealLogDao {
    @Query("SELECT * FROM meal_log_table")
    fun getAll(): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_log_table WHERE date = :date")
    fun getByDate(date: String): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: MealLog)

    @Query("DELETE FROM meal_log_table")
    suspend fun deleteAll()
}