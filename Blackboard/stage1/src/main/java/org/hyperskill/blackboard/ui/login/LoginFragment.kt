package org.hyperskill.blackboard.ui.login

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import org.hyperskill.blackboard.R
import org.hyperskill.blackboard.databinding.FragmentLoginBinding
import org.hyperskill.blackboard.util.Extensions.showToast

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels { LoginViewModel.Factory }
    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentLoginBinding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentLoginBinding.loginBtn.setOnClickListener {
            context?.showToast("Hello")
        }
    }
}