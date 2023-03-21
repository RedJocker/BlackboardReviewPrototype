package org.hyperskill.blackboard.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.hyperskill.blackboard.BlackboardApplication
import org.hyperskill.blackboard.R
import org.hyperskill.blackboard.data.model.Credential.Companion.putCredential
import org.hyperskill.blackboard.databinding.FragmentLoginBinding
import org.hyperskill.blackboard.network.login.dto.LoginResponse
import org.hyperskill.blackboard.util.Extensions.showToast
import java.io.IOException

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels {
        val application = requireActivity().application as BlackboardApplication
        LoginViewModel.Factory(application.loginClient)
    }

    private lateinit var binding: FragmentLoginBinding
    private val loginCallback = object : Callback {
        override fun onFailure(call: Call, e: IOException) = onLoginFailure(call, e)
        override fun onResponse(call: Call, response: Response) = onLoginResponse(call, response)
    }

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
            loginViewModel.makeLogin(username, pass, loginCallback)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onLoginResponse(call: Call, response: Response) {
        val loginResponse: LoginResponse = loginViewModel.toLoginResponse(response)

        activity?.runOnUiThread {
            println("onResponse $loginResponse")
            binding.helloTv.text = loginResponse.toString()
            when(loginResponse) {
                is LoginResponse.Success -> {
                    val args = Bundle().apply {
                        putCredential(loginResponse.toCredential())
                    }
                    findNavController()
                        .navigate(R.id.action_loginFragment_to_studentFragment, args)
                }
                else -> {}
            }
        }
    }
    @Suppress("UNUSED_PARAMETER")
    private fun onLoginFailure(call: Call, e: IOException) {
        println("onFailure")
        val traceToString = e.stackTraceToString()
        println(traceToString)

        activity?.runOnUiThread {
            binding.helloTv.text = e.message
        }
    }
}