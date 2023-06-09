package org.hyperskill.blackboard.ui.teacher

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.hyperskill.blackboard.BlackboardApplication
import org.hyperskill.blackboard.R
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.data.model.Credential.Companion.getCredential
import org.hyperskill.blackboard.data.model.Credential.Companion.putCredential
import org.hyperskill.blackboard.data.model.Student.Companion.putStudent
import org.hyperskill.blackboard.databinding.FragmentTeacherBinding


class TeacherFragment : Fragment() {

    private lateinit var binding: FragmentTeacherBinding
    lateinit var credentials: Credential

    private val studentsAdapter = StudentsRecyclerAdapter(listOf(), onStudentClick = { student ->
        val args = Bundle().apply {
            putStudent(student)
            putCredential(credentials)
        }
        findNavController()
            .navigate(R.id.action_teacherFragment_to_teacherStudentDetailsFragment, args)
    })

    private val teacherViewModel by viewModels<TeacherViewModel> {
        val activity = requireActivity()
        val application = activity.application as BlackboardApplication
        TeacherViewModel.Factory(application.teacherClient, Handler(activity.mainLooper))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        credentials = arguments!!.getCredential()
        binding = FragmentTeacherBinding.inflate(layoutInflater, container, false)
        teacherViewModel.fetchStudents(credentials)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.teacherName.text = "Teacher ${credentials.username}"
        binding.teacherStudentsList.adapter = studentsAdapter

        teacherViewModel.students.observe(viewLifecycleOwner) {
            studentsAdapter.students = it
        }
    }
}