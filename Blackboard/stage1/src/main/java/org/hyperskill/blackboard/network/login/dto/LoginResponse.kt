package org.hyperskill.blackboard.network.login.dto

import org.hyperskill.blackboard.data.model.Credential


sealed class LoginResponse {

    data class Success(val token: String, val role: Role) : LoginResponse(){

        fun toCredential(): Credential {
            return Credential(token, role)
        }
    }
    data class Fail(val message: String, val code: Int) : LoginResponse()


    enum class Role {
        TEACHER, STUDENT
    }
}