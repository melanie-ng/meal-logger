package com.example.meallogger

import androidx.lifecycle.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.Serializable

class MealLogViewModel(private val repository: MealLogRepository) : ViewModel(), Serializable {
    private val _logs = MutableLiveData<List<MealLog>>().apply { value = emptyList() }
    val logs: LiveData<List<MealLog>> get() = _logs

    fun getByDate(date: String) = viewModelScope.launch {
        _logs.value = repository.getByDate(date).firstOrNull() ?: emptyList()
    }

    fun update(mealLog: MealLog) = viewModelScope.launch {
        repository.update(mealLog)
    }

    fun insert(mealLog: MealLog) = viewModelScope.launch {
        repository.insert(mealLog)
    }

    fun delete(mealLog: MealLog) = viewModelScope.launch {
        repository.delete(mealLog)
        if (mealLog.date == SharedHelper.date) {
            _logs.value = repository.getByDate(SharedHelper.date).firstOrNull() ?: emptyList()
        }
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