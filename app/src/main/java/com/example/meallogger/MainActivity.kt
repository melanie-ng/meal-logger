package com.example.meallogger

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.sql.Date
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private val today = Date.valueOf(LocalDate.now().toString())
    private val mealLogViewModel: MealLogViewModel by viewModels {
        MealLogViewModelFactory((application as MealLogApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // prefill date field with today's date
        val dateField = findViewById<TextInputEditText>(R.id.dateFieldEditText)
        dateField.setText(today.toString())

        // hide keyboard when enter is pressed
        dateField.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(view)
            }
        }

        // floating action button that redirects user to Add Meal Log form
        val fab = findViewById<FloatingActionButton>(R.id.floating_action_button)
        fab.setOnClickListener {
            val i = Intent(this, AddMealActivity::class.java)
            startActivity(i)
        }

        updateLogs()
    }

    private fun updateLogs() {
        val tvNumOfMeals = findViewById<TextView>(R.id.numOfMeals)
        val tvNumOfSnacks = findViewById<TextView>(R.id.numOfSnacks)
        val tvTotalCalories = findViewById<TextView>(R.id.totalCalories)
        val mealLogs = findViewById<RecyclerView>(R.id.mealLogs)
        val noMealLogs = findViewById<TextView>(R.id.noMealLogs)

        tvNumOfMeals.text = "N/A"
        tvNumOfSnacks.text = "N/A"
        tvTotalCalories.text = "N/A"

        val mealLogsAdapter = MealLogsAdapter { showDetails(it) }
        mealLogs.adapter = mealLogsAdapter
        mealLogs.layoutManager = LinearLayoutManager(this)

        mealLogViewModel.allLogs.observe(this) { logs ->
            logs?.let { mealLogsAdapter.submitList(it) }
        }

        mealLogs.visibility = View.VISIBLE
        noMealLogs.visibility = View.GONE
    }

    private fun showDetails(meal: MealLog) {
        val mealLogDetailBottomSheet = MealLogDetailBottomSheet.newInstance(meal)
        mealLogDetailBottomSheet.show(supportFragmentManager, MealLogDetailBottomSheet.TAG)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}