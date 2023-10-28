package com.example.meallogger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MealLogsAdapter(private val data: List<MealLog>):
    RecyclerView.Adapter<MealLogsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealLogsAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.meal_log_layout, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val food = v.findViewById<TextView>(R.id.mealFood)
        val date = v.findViewById<TextView>(R.id.mealDate)
        val time = v.findViewById<TextView>(R.id.mealTime)
        val calories = v.findViewById<TextView>(R.id.mealCalories)

        fun bind(item: MealLog) {
            food.text = item.food
            date.text = item.date.toString()
            time.text = item.time.toString()

            // convert kJ to Cal
            if (item.unit == "kJ") {
                calories.text = String.format("%.0f", item.calories / 4.184)
            } else {
                calories.text = String.format("%.0f", item.calories.toDouble())
            }
        }
    }
}