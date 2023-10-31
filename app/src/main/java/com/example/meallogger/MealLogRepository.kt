package com.example.meallogger

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class MealLogRepository(private val mealLogDao: MealLogDao) {
    val allLogs: Flow<List<MealLog>> = mealLogDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(mealLog: MealLog) {
        mealLogDao.insert(mealLog)
    }
}