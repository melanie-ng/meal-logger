package com.example.meallogger

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class SharedHelper {
    companion object {
        private val localDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val datePickerFormatter = SimpleDateFormat("dd/MM/yyyy")
        val today: String = LocalDate.now().format(localDateFormatter)

        fun validDate(date: String): Boolean {
            try {
                localDateFormatter.parse(date)
            } catch(e: DateTimeParseException) {
                return false
            }

            return true
        }
    }
}