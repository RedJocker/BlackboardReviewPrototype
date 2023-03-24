package org.hyperskill.blackboard.ui.login

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.network.login.LoginClient
import org.hyperskill.blackboard.network.login.dto.LoginResponse
import org.hyperskill.blackboard.util.Util.callback
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

class LoginViewModel(private val loginClient: LoginClient, private val handler: Handler) : ViewModel() {


    private val loginCallback: Callback = callback(
        onFailure = ::onLoginFailure,
        onResponse = ::onLoginResponse
    )

    private val _messageLogin: MutableLiveData<String> = MutableLiveData("")
    val messageLogin: LiveData<String>
        get() = _messageLogin
    private val _credential: MutableLiveData<Credential?> = MutableLiveData(null)
    val credential: LiveData<Credential?>
        get() = _credential

    var call: Call? = null

    fun makeLogin(username: String, pass: String) {
        val rawPassBytes = pass.toByteArray(StandardCharsets.UTF_8)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val sha256HashPass = messageDigest.digest(rawPassBytes)
        val base64sha256HashPass = Base64.getEncoder().encodeToString(sha256HashPass)
        call = loginClient.loginRequest(username, base64sha256HashPass, loginCallback)
    }

    fun toLoginResponse(response: Response): LoginResponse {
        return if(response.code == 200) {
            loginClient.parse(response.body!!)
        } else {
            LoginResponse.Fail(response.message, response.code)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onLoginResponse(call: Call, response: Response) {
        val loginResponse: LoginResponse = toLoginResponse(response)
        println("onResponse $loginResponse")

        when(loginResponse) {
            is LoginResponse.Success -> {
                handler.post {
                    _credential.value = loginResponse.toCredential()
                }
            }
            is LoginResponse.Fail -> {
                handler.post {
                    _messageLogin.value = loginResponse.toString()
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onLoginFailure(call: Call, e: IOException) {
        println("onFailure")
        val traceToString = e.stackTraceToString()
        println(traceToString)

        handler.post {
            _messageLogin.value = e.message
        }
    }

    override fun onCleared() {
        call?.apply {
            cancel()
            call = null
        }
        super.onCleared()
    }

    fun clearCredential() {
        _credential.value = null
    }

    class Factory(private val loginClient: LoginClient, private val handler: Handler): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            return modelClass.getConstructor(LoginClient::class.java, Handler::class.java)
                    .newInstance(loginClient, handler)
        }
    }
//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val creationExtras: CreationExtras = this
//                val application = this[APPLICATION_KEY]
//                val blackboardApplication = application as BlackboardApplication
//
//                val loginClient = blackboardApplication.loginClient
//                LoginViewModel(loginClient)
//            }
//        }
//    }
}