package com.example.meallogger

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MealLogApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { MealLogDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { MealLogRepository(database.mealLogDao()) }
}