package org.hyperskill.blackboard.ui.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StudentViewModel : ViewModel() {

    private val tempMockGrades = listOf(60, 75, 55, 90)
    private val _grades: MutableLiveData<List<Int>> = MutableLiveData(tempMockGrades)
    val grades: LiveData<List<Int>>
        get() = _grades

    class Factory(): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor()
                .newInstance()
        }
    }
}