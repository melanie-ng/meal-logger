package com.example.meallogger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MealLogsAdapter(
    private val onItemClick : (MealLog) -> Unit
) : ListAdapter<MealLog, MealLogsAdapter.MealLogViewHolder>(MealLogsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealLogViewHolder {
        return MealLogViewHolder.create(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: MealLogViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class MealLogViewHolder(
        v: View,
        private val onItemClick : (MealLog) -> Unit
    ) : RecyclerView.ViewHolder(v) {
        val row = v.findViewById<LinearLayout>(R.id.mealLog)
        val food = v.findViewById<TextView>(R.id.mealFood)
        val date = v.findViewById<TextView>(R.id.mealDate)
        val time = v.findViewById<TextView>(R.id.mealTime)
        val calories = v.findViewById<TextView>(R.id.mealCalories)

        fun bind(item: MealLog) {
            food.text = item.food
            date.text = item.date
            time.text = item.time

            // convert kJ to Cal
            if (item.unit == "kJ") {
                calories.text = String.format("%.0f", item.calories / 4.184)
            } else {
                calories.text = String.format("%.0f", item.calories.toDouble())
            }

            // set up the event for when a row is clicked on in the MainActivity
            row.setOnClickListener {
                onItemClick.invoke(item)
            }
        }

        companion object {
            fun create(parent: ViewGroup, onItemClick: (MealLog) -> Unit): MealLogViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.meal_log_layout, parent, false)
                return MealLogViewHolder(view, onItemClick)
            }
        }
    }

    class MealLogsComparator : DiffUtil.ItemCallback<MealLog>() {
        override fun areItemsTheSame(oldItem: MealLog, newItem: MealLog): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MealLog, newItem: MealLog): Boolean {
            return oldItem.food == newItem.food &&
                   oldItem.date == newItem.date &&
                   oldItem.time == newItem.time &&
                   oldItem.calories == newItem.calories &&
                   oldItem.unit == newItem.unit &&
                   oldItem.category == newItem.category &&
                   oldItem.note == newItem.note
        }
    }
}