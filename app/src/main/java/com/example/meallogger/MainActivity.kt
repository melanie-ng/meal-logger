package com.example.meallogger

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import java.sql.Date
import java.sql.Time
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    // today's date
    val today = Date.valueOf(LocalDate.now().toString())

    // list of meal logs, temporary substitute for database
    val meals = mutableListOf(
        MealLog("Pasta", Date.valueOf("2023-10-23"), Time.valueOf("10:46:00"), 500, "Cal", "Meal", "Bolognese pasta"),
        MealLog("Donut", Date.valueOf("2023-10-23"), Time.valueOf("15:31:00"), 250, "Cal", "Snack", "Donutella from Levain"),
        MealLog("Fried Rice", Date.valueOf("2023-10-23"), Time.valueOf("18:45:00"), 435, "Cal", "Meal", "Cooked at home"),
        MealLog("Peanut Butter Sandwich", Date.valueOf("2023-10-24"), Time.valueOf("10:46:00"), 267, "Cal", "Meal", "Used Woolworths White Soft Sandwich Bread"),
        MealLog("Pizza", Date.valueOf("2023-10-24"), Time.valueOf("15:31:00"), 1050, "kJ", "Meal", "Had 1 slice of pepperoni pizza"),
        MealLog("Ramen", Date.valueOf("2023-10-24"), Time.valueOf("18:45:00"), 385, "kJ", "Meal", "Cintan curry flavour"),
        MealLog("Chocolate", Date.valueOf("2023-10-25"), Time.valueOf("10:46:00"), 85, "Cal", "Snack", "Cadbury milk chocolate"),
        MealLog("Ping Pong Crackers", Date.valueOf("2023-10-25"), Time.valueOf("15:31:00"), 250, "Cal", "Snack", "Had 3 pieces"),
        MealLog("Pringles", Date.valueOf("2023-10-25"), Time.valueOf("18:45:00"), 856, "Cal", "Snack", "")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // prefill date field with today's date
        val dateField = findViewById<TextInputEditText>(R.id.dateFieldEditText)
        dateField.setText(today.toString())

        // display logs for today only
        var filteredList = meals.filter {it.date == today}
        updateLogs(filteredList)

        // display appropriate logs when button is pressed
        val btnGo = findViewById<Button>(R.id.btnGo)
        btnGo.setOnClickListener {
            filteredList = meals.filter { it.date == Date.valueOf(dateField.getText().toString())}
            updateLogs(filteredList)

            // clear text field focus
            dateField.clearFocus()
            // hide keyboard
            hideKeyboard(dateField)
        }

        // hide keyboard when enter is pressed
        dateField.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(view)
            }
        }
    }

    private fun updateLogs(list: List<MealLog>) {
        val tvNumOfMeals = findViewById<TextView>(R.id.numOfMeals)
        val tvNumOfSnacks = findViewById<TextView>(R.id.numOfSnacks)
        val tvTotalCalories = findViewById<TextView>(R.id.totalCalories)
        val mealLogs = findViewById<RecyclerView>(R.id.mealLogs)
        val noMealLogs = findViewById<TextView>(R.id.noMealLogs)

        var numOfMeals = 0
        var numOfSnacks = 0
        var totalCalories = 0.0
        var tempCalories: Double

        // calculate total meals, snacks, and calories
        for (meal in list) {
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

        tvNumOfMeals.text = numOfMeals.toString()
        tvNumOfSnacks.text = numOfSnacks.toString()
        tvTotalCalories.text = String.format("%.0f", totalCalories)

        // determine whether logs or message should be displayed
        if (numOfMeals > 0 || numOfSnacks > 0) {
            val mealLogsAdapter = MealLogsAdapter(list)
            mealLogs.adapter = mealLogsAdapter
            mealLogs.layoutManager = LinearLayoutManager(this)

            // display a BottomSheetDialogFragment containing details related to the Meal Log clicked
            mealLogsAdapter.onItemClick = {
                val mealLogDetailBottomSheet = MealLogDetailBottomSheet.newInstance(it)
                mealLogDetailBottomSheet.show(supportFragmentManager, MealLogDetailBottomSheet.TAG)
            }

            mealLogs.visibility = View.VISIBLE
            noMealLogs.visibility = View.GONE
        } else {
            mealLogs.visibility = View.GONE
            noMealLogs.visibility = View.VISIBLE
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}