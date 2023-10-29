package com.example.meallogger

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Date
import java.sql.Time

@Parcelize
data class MealLog(val food: String, val date: Date, val time: Time, val calories: Int,
                   val unit: String, val category: String, val note: String): Parcelable {}