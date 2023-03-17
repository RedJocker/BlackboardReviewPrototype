package org.hyperskill.blackboard.internals.backend.service

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase

class LoginService(val moshi: Moshi) {

    val mapType = Types.newParameterizedType(
        Map::class.java,
        String::class.java,
        String::class.java
    )

    fun serve(request: RecordedRequest): MockResponse {

        if(request.method == "POST") {
            val bodyString = request.body.readUtf8()
            val mapBody = moshi.adapter<Map<String, String>>(mapType).fromJson(bodyString)
            println(mapBody)

            val requestPass = mapBody?.get("pass")
            val username = mapBody?.get("username")

            if (requestPass == null || username == null) { // Bad Request
                println("mock 400")
                return MockResponse()
                    .setResponseCode(400)
            }
            else {
                val user = MockUserDatabase.users[username]
                return when {
                    user == null -> {
                        println("mock 404") // Not Found
                        MockResponse()
                            .setStatus("404")
                    }
                    user.base64sha256HashPass == requestPass -> { // Ok
                        println("mock 200")
                        MockResponse()
                            .setBody("{" +
                                    "\"token\": \"${user.token}\", " +
                                    "\"role\": \"${user.role}\"" +
                                    "}")
                            .setResponseCode(200)
                            .addHeader("Content-Type", "application/json")
                    }
                    else -> {
                        println("mock 401")
                        MockResponse()
                            .setBody("Wrong pass $requestPass")
                            .setStatus("HTTP/1.1 401 Unauthorized")
                    }
                }
            }
        }

        println("mock 404") // Not Found
        return MockResponse()
            .setStatus("404")
    }
}