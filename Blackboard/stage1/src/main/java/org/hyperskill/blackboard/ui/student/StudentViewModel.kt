package org.hyperskill.blackboard.ui.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.hyperskill.blackboard.data.model.Credential

class StudentViewModel : ViewModel() {


    private val _grades: MutableLiveData<List<Int>> = MutableLiveData(listOf())
    val grades: LiveData<List<Int>>
        get() = _grades

    fun fetchGrades(credentials: Credential) {
        _grades.value = listOf(60, 75, 55, 90)
    }

    class Factory(): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor()
                .newInstance()
        }
    }
}