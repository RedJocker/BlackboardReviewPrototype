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

class StudentFragment : Fragment() {

    private val studentViewModel: StudentViewModel by viewModels {
        val activity = requireActivity()
        val application = activity.application as BlackboardApplication
        StudentViewModel.Factory(application.studentClient, Handler(activity.mainLooper))
    }
    private lateinit var binding: FragmentStudentBinding
    lateinit var credentials: Credential



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        credentials = arguments!!.getCredential()
        studentViewModel.fetchGrades(credentials)
        binding = FragmentStudentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val gradesRecyclerAdapter = GradesRecyclerAdapter(
                grades = emptyList(),
                onPredictionGradesChanged = { predictionGrades ->
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
                    }
                }
                grades.observe(viewLifecycleOwner) { gradesList ->
                    println("observe grades: $gradesList")
                    gradesRecyclerAdapter.grades = gradesList
                }
                predictionGrades.observe(viewLifecycleOwner) { predictionGradesList ->
                    println("observe predictionGrades $predictionGradesList")
                    val result =
                        if (predictionGradesList.isEmpty())
                            0
                        else
                            predictionGradesList.sum() / predictionGradesList.size
                    partialResultTV.text = "Partial Result: $result"
                }
            }
        }
    }
}