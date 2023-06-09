package org.hyperskill.blackboard.ui.student

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.hyperskill.blackboard.BlackboardApplication
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.data.model.Credential.Companion.getCredential
import org.hyperskill.blackboard.databinding.FragmentStudentBinding
import org.hyperskill.blackboard.databinding.StudentDetailBinding

class StudentFragment : Fragment() {

    private val studentViewModel: StudentViewModel by viewModels {
        val activity = requireActivity()
        val application = activity.application as BlackboardApplication
        StudentViewModel.Factory(application.studentClient, Handler(activity.mainLooper))
    }

    private lateinit var detailBinding: StudentDetailBinding
    lateinit var credentials: Credential

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        credentials = arguments!!.getCredential()
        studentViewModel.fetchGrades(credentials)
        val binding = FragmentStudentBinding.inflate(layoutInflater, container, false)
        detailBinding = StudentDetailBinding.bind(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailBinding.apply {
            val gradesRecyclerAdapter = GradesRecyclerAdapter(
                grades = emptyList(),
                onPredictionGradesChanged = { predictionGrades ->
                    println("onPredictionGradesChanged $predictionGrades")
                    studentViewModel.setPredictionGradesList(predictionGrades)
                }
            )
            gradesRV.adapter = gradesRecyclerAdapter
            studentNameTV.text = credentials.username
            studentViewModel.apply {
                networkErrorMessage.observe(viewLifecycleOwner) {
                    if(it.isNotBlank()) {
                        println("error: $it")
                        studentNameTV.error = "Error: $it"
                        studentNameTV.isFocusableInTouchMode = true
                        studentNameTV.isFocusable = true
                        studentNameTV.requestFocus()


                    } else {
                        studentNameTV.error = null
                    }
                }

                grades.observe(viewLifecycleOwner) { gradesList ->
                    println("observe grades: $gradesList")
                    gradesRecyclerAdapter.grades = gradesList
                    setPredictionGradesList(gradesList.map { if(it < 0) 0 else it })
                }

                partialResult.observe(viewLifecycleOwner) { partialResultString ->
                    println("observe partialResult: $partialResultString")
                    partialResultTV.text = partialResultString
                }

                examGradesPredictionEnabledToValue.observe(viewLifecycleOwner) { (isEtEnabled, predictionText) ->
                    examET.apply {
                        if(!isEtEnabled) {
                            setText(predictionText)
                            setPredictionExamGrade(-1)
                        }

                        isEnabled = isEtEnabled

                        if(isEtEnabled) {
                            setOnEditorActionListener { _, _, _ ->
                                val inputIntValue = text.toString().toIntOrNull() ?: -1
                                val normalizedPredictionGrade = if(inputIntValue > 100) {
                                    setText("100")
                                    100
                                } else inputIntValue
                                setPredictionExamGrade(normalizedPredictionGrade)
                                true
                            }
                        }
                    }
                }

                finalResult.observe(viewLifecycleOwner) {
                    finalResultTV.text = it
                }
            }
        }
    }
}