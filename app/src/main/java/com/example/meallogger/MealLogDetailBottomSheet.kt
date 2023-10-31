package com.example.meallogger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val ARG_MEAL = "meal"
private const val ARG_VIEW_MODEL = "vm"

class MealLogDetailBottomSheet : BottomSheetDialogFragment() {
    private var meal: MealLog? = null
    private var mealLogViewModel: MealLogViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            meal = it.getParcelable(ARG_MEAL)
            mealLogViewModel = it.getSerializable(ARG_VIEW_MODEL) as MealLogViewModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.meal_log_detail_bottom_sheet, container, false)

        val dateValue = view.findViewById<TextView>(R.id.dateValueSheet)
        dateValue.text = meal?.date.toString()
        val timeValue = view.findViewById<TextView>(R.id.timeValueSheet)
        timeValue.text = meal?.time.toString()
        val foodValue = view.findViewById<TextView>(R.id.foodValueSheet)
        foodValue.text = meal?.food.toString()
        val caloriesValue = view.findViewById<TextView>(R.id.caloriesValueSheet)
        caloriesValue.text = meal?.calories.toString()
        val categoryValue = view.findViewById<TextView>(R.id.categoryValueSheet)
        categoryValue.text = meal?.category.toString()
        val noteValue = view.findViewById<TextView>(R.id.noteValueSheet)
        if (meal?.note.toString().trim() == "") {
            noteValue.text = getString(R.string.no_note_recorded)
        } else {
            noteValue.text = meal?.note.toString()
        }

        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        btnDelete.setOnClickListener {
            meal?.let {
                log -> mealLogViewModel?.delete(log)
                dismiss()
            }
        }

        return view
    }

    companion object {
        const val TAG = "MealLogDetailBottomSheet"

        fun newInstance(meal: MealLog, mealLogViewModel: MealLogViewModel) =
            MealLogDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_MEAL, meal)
                    putSerializable(ARG_VIEW_MODEL, mealLogViewModel)
                }
            }
    }
}