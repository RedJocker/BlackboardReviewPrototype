package org.hyperskill.blackboard.data.model

import android.os.Bundle
import org.hyperskill.blackboard.network.login.dto.LoginResponse

data class Credential(val token: String, val role: LoginResponse.Role) {
    companion object {
        fun Bundle.putCredential(credential: Credential) {
            putString("token", credential.token)
            putString("role", credential.role.toString())
        }
        fun Bundle.getCredential(): Credential {
            val token = getString("token")!!
            val role = LoginResponse.Role.valueOf(getString("role")!!)
            return Credential(token, role)
        }
    }
}