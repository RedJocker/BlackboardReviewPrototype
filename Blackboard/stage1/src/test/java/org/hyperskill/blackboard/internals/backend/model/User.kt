package org.hyperskill.blackboard.internals.backend.model

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

class User(val userName: String, val role: String, val plainPass: String) {
    companion object {
        private val messageDigest = MessageDigest.getInstance("SHA-256")
    }

    private val plainPassBytes = plainPass.toByteArray(StandardCharsets.UTF_8)
    private val sha256HashPass = messageDigest.digest(plainPassBytes)
    val base64sha256HashPass = Base64.getEncoder().encodeToString(sha256HashPass)

    private val jwtAlg = Algorithm.HMAC256("testSecret")

    val token = JWT.create()
        .withIssuer("blackBoardApp")
        .withSubject(userName)
        .withClaim("ROLE", role)
        .sign(jwtAlg)
}