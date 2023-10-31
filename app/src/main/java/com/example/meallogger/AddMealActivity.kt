package com.example.meallogger

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class AddMealActivity: AppCompatActivity() {
    private val mealLogViewModel: MealLogViewModel by viewModels {
        MealLogViewModelFactory((application as MealLogApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meal)

        // prefill date and time fields
        val dateField = findViewById<TextInputEditText>(R.id.dateFieldEditText)
        dateField.setText(LocalDate.now().toString())
        val timeField = findViewById<TextInputEditText>(R.id.timeFieldEditText)
        timeField.setText(LocalTime.now().truncatedTo(ChronoUnit.SECONDS).toString())

        val foodField = findViewById<TextInputEditText>(R.id.foodFieldEditText)
        val caloriesField = findViewById<TextInputEditText>(R.id.caloriesFieldEditText)
        val unitField = findViewById<MaterialButtonToggleGroup>(R.id.toggleButton)
        val categoryField = findViewById<AutoCompleteTextView>(R.id.categoryFieldDropdown)
        val noteField = findViewById<TextInputEditText>(R.id.noteFieldEditText)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val log = MealLog(
                0,
                foodField.text.toString(),
                dateField.text.toString(),
                timeField.text.toString(),
                caloriesField.text.toString().toInt(),
                findViewById<Button>(unitField.checkedButtonId).text.toString(),
                categoryField.text.toString(),
                noteField.text.toString()
            )

            mealLogViewModel.insert(log)
            finish()
        }

        // redirects user back to main activity
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            finish()
        }
    }
}