package com.example.meallogger

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [MealLog::class], version = 1, exportSchema = false)
public abstract class MealLogDatabase : RoomDatabase() {

    abstract fun mealLogDao(): MealLogDao

    private class MealLogDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.mealLogDao())
                }
            }
        }

        suspend fun populateDatabase(mealLogDao: MealLogDao) {
            mealLogDao.deleteAll()

            val meals = mutableListOf(
                MealLog(1, "Pasta", "2023-10-23", "10:46:00", 500, "Cal", "Meal", "Bolognese pasta"),
                MealLog(2, "Donut", "2023-10-23", "15:31:00", 250, "Cal", "Snack", "Donutella from Levain"),
                MealLog(3, "Fried Rice", "2023-10-23", "18:45:00", 435, "Cal", "Meal", "Cooked at home"),
                MealLog(4, "Peanut Butter Sandwich", "2023-10-24", "10:46:00", 267, "Cal", "Meal", "Used Woolworths White Soft Sandwich Bread"),
                MealLog(5, "Pizza", "2023-10-24", "15:31:00", 1050, "kJ", "Meal", "Had 1 slice of pepperoni pizza"),
                MealLog(6, "Ramen", "2023-10-24", "18:45:00", 385, "kJ", "Meal", "Cintan curry flavour"),
                MealLog(7, "Chocolate", "2023-10-25", "10:46:00", 85, "Cal", "Snack", "Cadbury milk chocolate"),
                MealLog(8, "Ping Pong Crackers", "2023-10-25", "15:31:00", 250, "Cal", "Snack", "Had 3 pieces"),
                MealLog(9, "Pringles", "2023-10-30", "18:45:00", 856, "Cal", "Snack", "")
            )

            for (meal in meals) {
                mealLogDao.insert(meal)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MealLogDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): MealLogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealLogDatabase::class.java,
                    "meal_log_database"
                ).addCallback(MealLogDatabaseCallback(scope))
                 .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}