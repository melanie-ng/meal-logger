package com.example.meallogger

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalTime

class MealLogFormActivity: AppCompatActivity() {
    private val mealLogViewModel: MealLogViewModel by viewModels {
        MealLogViewModelFactory((application as MealLogApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_log_form)

        val existingLog = intent.getParcelableExtra<MealLog>("meal")

        val dateFieldLayout = findViewById<TextInputLayout>(R.id.textField)
        val dateField = findViewById<TextInputEditText>(R.id.dateFieldEditText)
        val timeFieldLayout = findViewById<TextInputLayout>(R.id.timeField)
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

        datePicker.addOnPositiveButtonClickListener {
            dateField.setText(SharedHelper.datePickerFormatter.format(datePicker.selection))
        }

        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(LocalTime.now().hour)
                .setMinute(LocalTime.now().minute)
                .setTitleText("Select time")
                .setInputMode(INPUT_MODE_CLOCK)
                .build()

        timePicker.addOnPositiveButtonClickListener {
            timeField.setText(String.format("%02d:%02d", timePicker.hour, timePicker.minute))
        }

        dateFieldLayout.errorIconDrawable = null

        dateFieldLayout.setEndIconOnClickListener {
            datePicker.show(supportFragmentManager, "tag")
        }

        timeFieldLayout.errorIconDrawable = null

        timeFieldLayout.setEndIconOnClickListener {
            timePicker.show(supportFragmentManager, "tag")
        }

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
            dateField.setText(SharedHelper.todayDate)
            timeField.setText(LocalTime.now().hour.toString() + ":" + LocalTime.now().minute.toString())

            btnAdd.setOnClickListener {
                var proceed: Boolean = true

                if (!SharedHelper.validDate(dateField.text.toString())) {
                    proceed = false
                    dateFieldLayout.error = "Please enter a valid date"

                    dateField.addTextChangedListener {
                        dateFieldLayout.error = null
                    }
                }

                if (!SharedHelper.validTime(timeField.text.toString())) {
                    proceed = false
                    timeFieldLayout.error = "Please enter a valid time"

                    timeField.addTextChangedListener {
                        timeFieldLayout.error = null
                    }
                }

                if (proceed) {
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
        }

        // redirects user back to main activity
        btnCancel.setOnClickListener {
            finish()
        }
    }
}