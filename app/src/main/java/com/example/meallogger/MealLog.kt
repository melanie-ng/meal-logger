package com.example.meallogger

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "meal_log_table")
@Parcelize
data class MealLog(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "food") val food: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "calories") val calories: Int,
    @ColumnInfo(name = "unit") val unit: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "note") val note: String?
): Parcelable {}