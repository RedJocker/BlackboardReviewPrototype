package org.hyperskill.blackboard

import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.hyperskill.blackboard.request.LoginRequest

class BlackBoardClient(private val moshi: Moshi) {

    val client = OkHttpClient()

    var baseurl = "https://72ad1658-e2bb-4459-9ea2-6448fa833318.mock.pstmn.io"

    fun loginRequest(username: String, pass: String): Request {
        val mediaType = "application/json".toMediaType()
        val body = moshi.adapter(LoginRequest::class.java)
            .toJson(LoginRequest(username, pass))
            .toRequestBody(mediaType)

        return Request.Builder()
            .url(baseurl + "login/")
            .post(body)
            .build()
    }
}