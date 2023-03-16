package org.hyperskill.blackboard

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class BlackBoardClient {

    val client = OkHttpClient()

    var baseurl = "https://72ad1658-e2bb-4459-9ea2-6448fa833318.mock.pstmn.io"

    fun simpleTestPostRequest(resourcePath: String, rawPass: String): Request {
        val mediaType = "application/json".toMediaType()
        val body = "{\"pass\":\"$rawPass\"}".toRequestBody(mediaType)

        return Request.Builder()
            .url(baseurl + resourcePath)
            .post(body)
            .build()
    }

}