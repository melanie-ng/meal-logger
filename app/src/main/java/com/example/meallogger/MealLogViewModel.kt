package com.example.meallogger

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.io.Serializable

class MealLogViewModel(private val repository: MealLogRepository) : ViewModel(), Serializable {
    val allLogs: LiveData<List<MealLog>> = repository.allLogs.asLiveData()
    lateinit var logs: LiveData<List<MealLog>>

    fun getByDate(date: String) = viewModelScope.launch {
        logs = repository.getByDate(date).asLiveData()
    }

    fun insert(mealLog: MealLog) = viewModelScope.launch {
        repository.insert(mealLog)
    }

    fun delete(mealLog: MealLog) = viewModelScope.launch {
        repository.delete(mealLog)
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