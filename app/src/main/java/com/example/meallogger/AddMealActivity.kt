package com.example.meallogger

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class AddMealActivity: AppCompatActivity() {
    // current date and time
    val currentDate = LocalDate.now().toString()
    val currentTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS).toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meal)

        // prefill date and time fields
        val dateField = findViewById<TextInputEditText>(R.id.dateFieldEditText)
        dateField.setText(currentDate)
        val timeField = findViewById<TextInputEditText>(R.id.timeFieldEditText)
        timeField.setText(currentTime)

        // redirects user back to main activity
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            finish()
        }
    }
}