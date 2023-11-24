package com.example.meallogger

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class SharedHelper {
    companion object {
        private val localDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        private val localTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val datePickerFormatter = SimpleDateFormat("dd/MM/yyyy")
        val todayDate: String = LocalDate.now().format(localDateFormatter)
        var date: String = todayDate

        fun validDate(date: String): Boolean {
            try {
                localDateFormatter.parse(date)
            } catch(e: DateTimeParseException) {
                return false
            }

            return true
        }

        fun validTime(time: String): Boolean {
            try {
                localTimeFormatter.parse(time)
            } catch(e: DateTimeParseException) {
                return false
            }

            return true
        }
    }
}