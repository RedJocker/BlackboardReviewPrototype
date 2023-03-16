package org.hyperskill.blackboard.internals.backend

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.mindrot.jbcrypt.BCrypt
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

class LoginService(val moshi: Moshi) {

    val mapType = Types.newParameterizedType(
        Map::class.java,
        String::class.java,
        String::class.java
    )

    val base64Pass: String = MessageDigest.getInstance("SHA-256")
        .digest("1234".toByteArray(StandardCharsets.UTF_8)).let {
            Base64.getEncoder().encodeToString(it)
        }


    fun serve(request: RecordedRequest): MockResponse {

        if(request.method == "POST") {
            val bodyString = request.body.readUtf8()
            val mapBody = moshi.adapter<Map<String, String>>(mapType).fromJson(bodyString)
            println(mapBody)

            val pass = mapBody?.get("pass")

            return if (pass == null) { // Bad Request
                println("mock 400")
                MockResponse()
                    .setResponseCode(400)
            }
            else {
                if (BCrypt.checkpw(base64Pass, pass)) { // Ok
                    println("mock 200")
                    MockResponse()
                        .setBody("{\"token\": \"abc\"}")
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                } else {
                    println("mock 401")
                    MockResponse()
                        .setBody("Wrong pass $pass")
                        .setStatus("HTTP/1.1 401 Unauthorized")
                }
            }
        }

        println("mock 404") // Not Found
        return MockResponse()
            .setStatus("404")
    }
}