package org.hyperskill.blackboard.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import okhttp3.Call
import okhttp3.Callback
import org.hyperskill.blackboard.LoginClient
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

class LoginViewModel(private val loginClient: LoginClient) : ViewModel() {

    var call: Call? = null
    fun makeLogin(username: String, pass: String, callback: Callback) {
        val rawPassBytes = pass.toByteArray(StandardCharsets.UTF_8)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val sha256HashPass = messageDigest.digest(rawPassBytes)
        val base64sha256HashPass = Base64.getEncoder().encodeToString(sha256HashPass)
        call = loginClient.loginRequest(username, base64sha256HashPass, callback)
    }

    override fun onCleared() {
        call?.apply {
            cancel()
            call = null
        }
        super.onCleared()
    }

    class Factory(private val loginClient: LoginClient): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            return modelClass.getConstructor(LoginClient::class.java)
                    .newInstance(loginClient)
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