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
import java.util.*

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
        val foodFieldLayout = findViewById<TextInputLayout>(R.id.foodField)
        val foodField = findViewById<TextInputEditText>(R.id.foodFieldEditText)
        val caloriesFieldLayout = findViewById<TextInputLayout>(R.id.caloriesField)
        val caloriesField = findViewById<TextInputEditText>(R.id.caloriesFieldEditText)
        val unitField = findViewById<MaterialButtonToggleGroup>(R.id.toggleButton)
        val categoryFieldLayout = findViewById<TextInputLayout>(R.id.categoryField)
        val categoryField = findViewById<AutoCompleteTextView>(R.id.categoryFieldDropdown)
        val noteField = findViewById<TextInputEditText>(R.id.noteFieldEditText)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        var datePicker: MaterialDatePicker<Long>?
        var timePicker: MaterialTimePicker?

        dateFieldLayout.errorIconDrawable = null
        timeFieldLayout.errorIconDrawable = null
        foodFieldLayout.errorIconDrawable = null
        caloriesFieldLayout.errorIconDrawable = null

        if (existingLog == null) {
            // set up add meal log form
            val todayTimeHour = LocalTime.now().hour
            val todayTimeMinute = LocalTime.now().minute

            datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(todayTimeHour)
                    .setMinute(todayTimeMinute)
                    .setTitleText("Select time")
                    .setInputMode(INPUT_MODE_CLOCK)
                    .build()

            dateField.setText(SharedHelper.todayDate)
            timeField.setText(String.format("%02d:%02d", todayTimeHour, todayTimeMinute))
        } else {
            // set up edit meal log form
            btnAdd.text = getString(R.string.edit)

            SharedHelper.datePickerFormatter.timeZone = TimeZone.getTimeZone("UTC")

            val logDate = SharedHelper.datePickerFormatter.parse(existingLog.date).time
            val logTime = LocalTime.parse(existingLog.time)
            val logTimeHour = logTime.hour
            val logTimeMinute = logTime.minute

            datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(logDate)
                .build()

            timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(logTimeHour)
                .setMinute(logTimeMinute)
                .setTitleText("Select time")
                .setInputMode(INPUT_MODE_CLOCK)
                .build()

            dateField.setText(existingLog.date)
            timeField.setText(existingLog.time)
            foodField.setText(existingLog.food)
            caloriesField.setText(existingLog.calories.toString())
            if (existingLog.unit == "kJ") {
                unitField.check(R.id.button2)
            }
            categoryField.setText(existingLog.category, false)
            noteField.setText(existingLog.note)
        }

        datePicker.addOnPositiveButtonClickListener {
            dateField.setText(SharedHelper.datePickerFormatter.format(datePicker.selection))
        }

        dateFieldLayout.setEndIconOnClickListener {
            datePicker.show(supportFragmentManager, "tag")
        }

        timePicker.addOnPositiveButtonClickListener {
            timeField.setText(String.format("%02d:%02d", timePicker.hour, timePicker.minute))
        }

        timeFieldLayout.setEndIconOnClickListener {
            timePicker.show(supportFragmentManager, "tag")
        }

        btnAdd.setOnClickListener {
            var proceed = true

            val id = existingLog?.id ?: 0
            val food = foodField.text.toString().trim()
            val date = dateField.text.toString().trim()
            val time = timeField.text.toString().trim()
            val calories = caloriesField.text.toString()
            val unit = findViewById<Button>(unitField.checkedButtonId).text.toString()
            val category = categoryField.text.toString()
            val note = noteField.text.toString().trim()

            if (food == "") {
                proceed = false

                foodFieldLayout.error = "Please enter the name of the food"

                foodField.addTextChangedListener {
                    foodFieldLayout.error = null
                }
            }

            if (!SharedHelper.validDate(date)) {
                proceed = false
                dateFieldLayout.error = "Please enter a valid date"

                dateField.addTextChangedListener {
                    dateFieldLayout.error = null
                }
            }

            if (!SharedHelper.validTime(time)) {
                proceed = false
                timeFieldLayout.error = "Please enter a valid time"

                timeField.addTextChangedListener {
                    timeFieldLayout.error = null
                }
            }

            if (calories == "") {
                proceed = false

                caloriesFieldLayout.error = "Please enter the number of calories"

                caloriesField.addTextChangedListener {
                    caloriesFieldLayout.error = null
                }
            }

            if (category == "") {
                proceed = false

                categoryFieldLayout.error = "Please select a category"

                categoryField.addTextChangedListener {
                    categoryFieldLayout.error = null
                }
            }

            if (proceed) {
                val log = MealLog(id, food, date, time, calories.toInt(), unit, category, note)

                if (existingLog == null) {
                    mealLogViewModel.insert(log)
                } else {
                    mealLogViewModel.update(log)
                }

                finish()
            }
        }

        // redirects user back to main activity
        btnCancel.setOnClickListener {
            finish()
        }
    }
}