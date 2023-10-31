package com.example.meallogger

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class MealLogViewModel(private val repository: MealLogRepository) : ViewModel() {

    val allLogs: LiveData<List<MealLog>> = repository.allLogs.asLiveData()

    fun insert(mealLog: MealLog) = viewModelScope.launch {
        repository.insert(mealLog)
    }
}

class MealLogViewModelFactory(private val repository: MealLogRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealLogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealLogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}