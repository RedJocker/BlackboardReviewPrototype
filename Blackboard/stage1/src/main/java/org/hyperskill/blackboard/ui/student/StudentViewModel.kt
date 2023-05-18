package org.hyperskill.blackboard.ui.student

import android.os.Handler
import androidx.lifecycle.*
import okhttp3.Call
import okhttp3.Response
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.network.student.StudentClient
import org.hyperskill.blackboard.network.student.dto.GradesResponse
import org.hyperskill.blackboard.util.Extensions.combineWith
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

    private var _predictionExamGrade: MutableLiveData<Int> = MutableLiveData(-1)
    val predictionExamGrade: LiveData<Int>
        get() = _predictionExamGrade

    private var _examGrades: MutableLiveData<Int> = MutableLiveData(-1)
    val examGrade: LiveData<Int>
        get() = _examGrades

    val partialGrade = grades.map { grades ->
        if(grades.isEmpty())
            0
        else
            grades.sumOf { if (it < 0) 0 else it } / grades.size
    }.also { println("partialGrade ${it.value}") }

    val predictionPartialGrade = predictionGrades.map { predictionGradesList ->
        if(predictionGradesList.isEmpty())
            0
        else
            predictionGradesList.sum() / predictionGradesList.size
    }.also { println("predictionPartialGrade ${it.value}") }

    val finalGrade = partialGrade.combineWith(examGrade) { partial, exam ->
        when {
            partial == null -> null
            exam == null -> partial
            exam < 0 -> partial
            else -> (partial + exam) / 2
        }.also { println("finalGrade $it") }
    }

    val predictionFinalGrade = predictionPartialGrade.combineWith(predictionExamGrade) { partial, exam ->
            when {
                partial == null -> null
                exam == null -> partial
                exam < 0 -> partial
                else -> (partial + exam) / 2
            }.also { println("predictionFinalGrade $it") }
    }

    val examGradesPredictionEnabledToValue =
        examGrade.combineWith(predictionPartialGrade) { examGrade, predictionPartialGrade ->
            when {
                examGrade == null || predictionPartialGrade == null -> true to ""
                examGrade > 0 -> false to "$examGrade"
                predictionPartialGrade !in 30 until 70 -> false to ""
                else -> true to ""
            }.also { println("examGrade $it") }
    }

    val partialResult = partialGrade.combineWith(predictionPartialGrade) {partialGrade, predictionPartialGrade ->
        val predictionString = if(predictionPartialGrade == partialGrade) "" else " ($predictionPartialGrade)"
        "Partial Result: $partialGrade$predictionString"
    }.also { println("partialResult ${it.value}") }

    val finalResult = finalGrade.combineWith(predictionFinalGrade) { finalGrade, predictionFinalGrade ->
        val predictionString = if(finalGrade == predictionFinalGrade) "" else " ($predictionFinalGrade)"
        val finalGradeString = if(finalGrade != null) "$finalGrade" else ""
        "Final Result: $finalGradeString$predictionString"
    }

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

    fun setPredictionExamGrade(predictionExamGrade: Int) {
        _predictionExamGrade.value = predictionExamGrade
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
                    val gradesResponse = studentClient.parseGrades(response.body)
                    if(gradesResponse == null || gradesResponse is GradesResponse.Fail) {
                        post {
                            _networkErrorMessage.value =
                                "server error, invalid response body ${response.body?.string()}"
                        }
                    } else {
                        post {
                            (gradesResponse as? GradesResponse.Success)?.also {

                                _grades.value = if(it.exam > 0) {
                                    it.grades.map { grade -> if(grade < 0) 0 else grade }
                                } else {
                                    it.grades
                                }
                                _examGrades.value = it.exam
                                _predictionExamGrade.value = it.exam
                            }
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