package org.hyperskill.blackboard.network.login.dto

import android.os.Bundle


sealed class LoginResponse{

    data class Success(val token: String, val role: Role) : LoginResponse(){

        companion object {
            fun Bundle.putLoginSuccess(loginSuccess: Success) {
                putString("token", loginSuccess.token)
                putString("role", loginSuccess.role.toString())
            }
            fun Bundle.getLoginSuccess(): Success {
                val token = getString("token")!!
                val role = Role.valueOf(getString("role")!!)
                return Success(token, role)
            }
        }
    }
    data class Fail(val message: String, val code: Int) : LoginResponse()


    enum class Role {
        TEACHER, STUDENT
    }
}