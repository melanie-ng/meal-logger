package com.example.meallogger

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private val mealLogViewModel: MealLogViewModel by viewModels {
        MealLogViewModelFactory((application as MealLogApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dateFieldLayout = findViewById<TextInputLayout>(R.id.dateField)
        val dateField = findViewById<TextInputEditText>(R.id.dateFieldEditText)
        val btnGo = findViewById<Button>(R.id.btnGo)
        val fab = findViewById<FloatingActionButton>(R.id.floating_action_button)

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        datePicker.addOnPositiveButtonClickListener {
            dateField.setText(SharedHelper.datePickerFormatter.format(datePicker.selection))
        }

        // display today's logs when app initially launches
        updateLogs(SharedHelper.todayDate)

        // prevent error icon from hiding the calendar icon (date picker)
        dateFieldLayout.errorIconDrawable = null

        dateFieldLayout.setEndIconOnClickListener {
            datePicker.show(supportFragmentManager, "tag")
        }

        // prefill date field with today's date
        dateField.setText(SharedHelper.todayDate)

        // hide keyboard when enter is pressed
        dateField.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(view)
            }
        }

        // display appropriate logs when button is pressed
        btnGo.setOnClickListener {
            val input = dateField.text.toString().trim()

            if (!SharedHelper.validDate(input)) {
                dateFieldLayout.error = "Please enter a valid date"

                dateField.addTextChangedListener {
                    dateFieldLayout.error = null
                }
            } else {
                updateLogs(input)

                dateField.clearFocus()
                hideKeyboard(dateField)

                Toast.makeText(
                    this,
                    "Displaying logs for $input",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // floating action button that redirects user to Add Meal Log form
        fab.setOnClickListener {
            val i = Intent(this, MealLogFormActivity::class.java)
            startActivity(i)
        }
    }

    private fun updateLogs(date: String) {
        val noMealLogs = findViewById<TextView>(R.id.noMealLogs)
        val mealLogs = findViewById<RecyclerView>(R.id.mealLogs)

        val mealLogsAdapter = MealLogsAdapter { showDetails(it) }
        mealLogs.adapter = mealLogsAdapter
        mealLogs.layoutManager = LinearLayoutManager(this)

        mealLogViewModel.getByDate(date)

        mealLogViewModel.logs.observe(this) { logs ->
            logs?.let {
                val tvNumOfMeals = findViewById<TextView>(R.id.numOfMeals)
                val tvNumOfSnacks = findViewById<TextView>(R.id.numOfSnacks)
                val tvTotalCalories = findViewById<TextView>(R.id.totalCalories)

                var numOfMeals = 0
                var numOfSnacks = 0
                var totalCalories = 0.0

                if (it.isEmpty()) {
                    mealLogs.visibility = View.GONE
                    noMealLogs.visibility = View.VISIBLE
                } else {
                    mealLogsAdapter.submitList(it)
                    var tempCalories: Double

                    // calculate total meals, snacks, and calories
                    for (meal in it) {
                        if (meal.category == "Meal") {
                            numOfMeals++
                        } else {
                            numOfSnacks++
                        }

                        // convert kJ to Cal
                        if (meal.unit == "kJ") {
                            tempCalories = meal.calories / 4.184
                        } else {
                            tempCalories = meal.calories.toDouble()
                        }

                        totalCalories += tempCalories
                    }

                    mealLogs.visibility = View.VISIBLE
                    noMealLogs.visibility = View.GONE
                }

                tvNumOfMeals.text = numOfMeals.toString()
                tvNumOfSnacks.text = numOfSnacks.toString()
                tvTotalCalories.text = String.format("%.0f", totalCalories)
            }
        }
    }

    private fun showDetails(meal: MealLog) {
        val mealLogDetailBottomSheet = MealLogDetailBottomSheet.newInstance(meal, mealLogViewModel)
        mealLogDetailBottomSheet.show(supportFragmentManager, MealLogDetailBottomSheet.TAG)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
