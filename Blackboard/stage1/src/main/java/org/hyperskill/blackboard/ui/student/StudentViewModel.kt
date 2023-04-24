package org.hyperskill.blackboard.ui.student

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import okhttp3.Call
import okhttp3.Response
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.network.student.StudentClient
import org.hyperskill.blackboard.util.Util.callback
import java.io.IOException


class StudentViewModel(
    private val studentClient: StudentClient, private val handler: Handler) : ViewModel() {


    private var _predictionGrades: MutableLiveData<List<Int>> = MutableLiveData(listOf())
    val predictionGrades: LiveData<List<Int>>
        get() = _predictionGrades

    private val _grades: MutableLiveData<List<Int>> = MutableLiveData(listOf())
    val grades: LiveData<List<Int>>
        get() = _grades

    private val _networkErrorMessage: MutableLiveData<String> = MutableLiveData("")
    val networkErrorMessage: LiveData<String>
        get() = _networkErrorMessage

    private var fetchGradesCall : Call? = null

    fun fetchGrades(credential: Credential) {
        fetchGradesCall = studentClient.fetchGrades(credential, callback(
            onFailure = ::onFetchGradesFailure,
            onResponse = ::onFetchGradesResponse
        ))
    }

    fun setPredictionGradesList(predictionGrades: List<Int>) {
        _predictionGrades.value = predictionGrades
    }

    private fun onFetchGradesFailure(call: Call, e: IOException) {
        println(call)
        handler.post {
            _networkErrorMessage.value = e.message
        }
    }

    private fun onFetchGradesResponse(call: Call, response: Response) {
        println(call)
        handler.apply {
            println(response)
            when(response.code) {
                200 -> {
                    val grades = studentClient.parseGrades(response.body)
                    if(grades == null) {
                        post {
                            _networkErrorMessage.value =
                                "server error, invalid response body ${response.body?.string()}"
                        }
                    } else {
                        post {
                            _grades.value = grades
                        }
                    }
                }
                else -> {
                    post {
                        _networkErrorMessage.value = "${response.code} ${response.message}"
                    }
                }
            }
        }
    }

    class Factory(val studentClient: StudentClient, val handler: Handler): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(StudentClient::class.java, Handler::class.java)
                .newInstance(studentClient, handler)
        }
    }

    override fun onCleared() {
        fetchGradesCall?.apply {
            cancel()
            fetchGradesCall = null
        }
        super.onCleared()
    }
}