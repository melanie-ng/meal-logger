package com.example.meallogger

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MealLogFormActivity: AppCompatActivity() {
    private val localDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val datePickerFormatter = SimpleDateFormat("dd/MM/yyyy")
    private val mealLogViewModel: MealLogViewModel by viewModels {
        MealLogViewModelFactory((application as MealLogApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_log_form)

        val dateField = findViewById<TextInputEditText>(R.id.dateFieldEditText)
        val timeField = findViewById<TextInputEditText>(R.id.timeFieldEditText)
        val foodField = findViewById<TextInputEditText>(R.id.foodFieldEditText)
        val caloriesField = findViewById<TextInputEditText>(R.id.caloriesFieldEditText)
        val unitField = findViewById<MaterialButtonToggleGroup>(R.id.toggleButton)
        val categoryField = findViewById<AutoCompleteTextView>(R.id.categoryFieldDropdown)
        val noteField = findViewById<TextInputEditText>(R.id.noteFieldEditText)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        val dateFieldLayout = findViewById<TextInputLayout>(R.id.textField)
        dateFieldLayout.setEndIconOnClickListener {
            datePicker.show(supportFragmentManager, "tag")
        }

        datePicker.addOnPositiveButtonClickListener {
            dateField.setText(datePickerFormatter.format(datePicker.selection))
        }

        val existingLog = intent.getParcelableExtra<MealLog>("meal")

        if (existingLog != null) {
            dateField.setText(existingLog.date)
            timeField.setText(existingLog.time)
            foodField.setText(existingLog.food)
            caloriesField.setText(existingLog.calories.toString())
            if (existingLog.unit == "kJ") {
                unitField.check(R.id.button2)
            }
            categoryField.setText(existingLog.category, false)
            noteField.setText(existingLog.note)

            btnAdd.text = getString(R.string.edit)
            btnAdd.setOnClickListener {
                val log = MealLog(
                    existingLog.id,
                    foodField.text.toString(),
                    dateField.text.toString(),
                    timeField.text.toString(),
                    caloriesField.text.toString().toInt(),
                    findViewById<Button>(unitField.checkedButtonId).text.toString(),
                    categoryField.text.toString(),
                    noteField.text.toString()
                )

                mealLogViewModel.update(log)
                finish()
            }
        } else {
            // prefill date and time fields
            dateField.setText(LocalDate.now().format(localDateFormatter))
            timeField.setText(LocalTime.now().truncatedTo(ChronoUnit.SECONDS).toString())

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
        }

        // redirects user back to main activity
        btnCancel.setOnClickListener {
            finish()
        }
    }
}