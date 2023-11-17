package com.example.meallogger

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.shrikanthravi.collapsiblecalendarview.data.Day
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import java.time.LocalDate
import java.util.*


class MainActivity : AppCompatActivity() {
    private val mealLogViewModel: MealLogViewModel by viewModels {
        MealLogViewModelFactory((application as MealLogApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendar = findViewById<CollapsibleCalendar>(R.id.calendarView)
        val fab = findViewById<FloatingActionButton>(R.id.floating_action_button)

        // display today's logs when app initially launches
        updateLogs(SharedHelper.todayDate)

        calendar.setCalendarListener(object: CollapsibleCalendar.CalendarListener {
            override fun onDaySelect() {
                val day = calendar.selectedDay!!
                val input = String.format("%02d/%02d/%02d", day.day, day.month + 1, day.year)

                updateLogs(input)
            }

            override fun onItemClick(v: View) {}
            override fun onClickListener() {}
            override fun onDataUpdate() {}
            override fun onDayChanged() {}
            override fun onWeekChange(position: Int) {}
            override fun onMonthChange() {}
        })

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
