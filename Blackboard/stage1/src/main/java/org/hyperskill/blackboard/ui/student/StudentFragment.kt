package org.hyperskill.blackboard.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.hyperskill.blackboard.databinding.FragmentStudentBinding
import org.hyperskill.blackboard.network.login.dto.LoginResponse
import org.hyperskill.blackboard.network.login.dto.LoginResponse.Success.Companion.getLoginSuccess
import org.hyperskill.blackboard.util.Extensions.showToast

class StudentFragment : Fragment() {

    private val studentViewModel: StudentViewModel by viewModels {
        StudentViewModel.Factory()
    }
    private lateinit var binding: FragmentStudentBinding
    lateinit var credentials: LoginResponse.Success



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStudentBinding.inflate(layoutInflater, container, false)
        credentials = arguments!!.getLoginSuccess()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studentViewModel.grades.observe(viewLifecycleOwner) {
            println("observe grades: $it")
            binding.studentGradesTempView.text = "Grades: $it"
        }

        binding.studentHelloButton.setOnClickListener {
            context?.showToast("Hello $credentials")
        }
    }
}