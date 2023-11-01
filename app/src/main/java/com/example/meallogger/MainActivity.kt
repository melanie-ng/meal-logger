package com.example.meallogger

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private val today = LocalDate.now().toString()
    private val mealLogViewModel: MealLogViewModel by viewModels {
        MealLogViewModelFactory((application as MealLogApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateLogs(today)

        // floating action button that redirects user to Add Meal Log form
        val fab = findViewById<FloatingActionButton>(R.id.floating_action_button)
        fab.setOnClickListener {
            val i = Intent(this, MealLogFormActivity::class.java)
            startActivity(i)
        }

        // prefill date field with today's date
        val dateField = findViewById<TextInputEditText>(R.id.dateFieldEditText)
        dateField.setText(today)

        // hide keyboard when enter is pressed
        dateField.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(view)
            }
        }

        // display appropriate logs when button is pressed
        val btnGo = findViewById<Button>(R.id.btnGo)
        btnGo.setOnClickListener {
            updateLogs(dateField.text.toString())

            // clear text field focus
            dateField.clearFocus()
            // hide keyboard
            hideKeyboard(dateField)
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