package org.hyperskill.blackboard.internals.backend

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

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

            val pass = mapBody?.get("pass")

            when(pass) {
                null -> { // Bad Request
                    println("mock 400")
                    return MockResponse()
                        .setResponseCode(400)
                }
                "1234" -> { // Ok
                    println("mock 200")
                    return MockResponse()
                        .setBody("{\"token\": \"abc\"}")
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                }
                else -> { // Unauthorized
                    println("mock 401")
                    return MockResponse()
                        .setResponseCode(401)
                }
            }
        }

        println("mock 404") // Not Found
        return MockResponse()
            .setStatus("404")
    }
}