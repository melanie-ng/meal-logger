package com.example.meallogger

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class MealLogRepository(private val mealLogDao: MealLogDao) {
    val allLogs: Flow<List<MealLog>> = mealLogDao.getAll()

    fun getByDate(date: String): Flow<List<MealLog>> {
        return mealLogDao.getByDate(date)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(mealLog: MealLog) {
        mealLogDao.update(mealLog)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(mealLog: MealLog) {
        mealLogDao.delete(mealLog)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(mealLog: MealLog) {
        mealLogDao.insert(mealLog)
    }
}