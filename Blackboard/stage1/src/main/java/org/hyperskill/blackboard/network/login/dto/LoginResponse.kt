package org.hyperskill.blackboard.network.login.dto

sealed class LoginResponse {

    class Success(val token: String, val role: Role) : LoginResponse()
    class Fail(message: String, code: Int) : LoginResponse()


    enum class Role {
        TEACHER, STUDENT
    }
}