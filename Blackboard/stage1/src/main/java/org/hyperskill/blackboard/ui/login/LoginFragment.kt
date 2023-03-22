package org.hyperskill.blackboard.ui.login

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
import org.hyperskill.blackboard.data.model.Credential.Role
import org.hyperskill.blackboard.data.model.Credential.Companion.putCredential
import org.hyperskill.blackboard.databinding.FragmentLoginBinding
import org.hyperskill.blackboard.util.Extensions.showToast

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels {
        val activity = requireActivity()
        val application = activity.application as BlackboardApplication
        LoginViewModel.Factory(application.loginClient, Handler(activity.mainLooper))
    }

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            context?.showToast("click")
            val username = binding.loginUsernameEt.text.toString()
            val pass = binding.loginPassEt.text.toString()
            loginViewModel.makeLogin(username, pass)
        }

        loginViewModel.apply {
            messageLogin.observe(viewLifecycleOwner) { message ->
                if(message.isNotBlank()) {
                    binding.helloTv.text = message
                }
            }
            credential.observe(viewLifecycleOwner) { maybeCredential ->
                maybeCredential?.also { credential ->
                    when(credential.role) {
                        Role.STUDENT -> {
                            val args = Bundle().apply {
                                putCredential(credential)
                            }
                            loginViewModel.clearCredential()
                            findNavController()
                                .navigate(R.id.action_loginFragment_to_studentFragment, args)
                        }
                        Role.TEACHER -> {

                        }
                    }
                }
            }
        }
    }
}