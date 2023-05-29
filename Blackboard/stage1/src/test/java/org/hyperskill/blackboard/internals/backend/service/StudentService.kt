package org.hyperskill.blackboard.internals.backend.service

import com.squareup.moshi.Moshi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class StudentService(val moshi: Moshi): Service {
    override fun serve(request: RecordedRequest): MockResponse {
        println(request)

        if (request.method == "GET") {
            return when(request.path) {
                "/student/Martin/grade" -> {
                    MockResponse().setStatus("HTTP/1.1 504 Gateway Timeout")
                }
                else -> {
                    println("mock 404") // Not Found
                    return MockResponse()
                        .setStatus("404")
                }
            }
        } else {
            println("mock 404") // Not Found
            return MockResponse()
                .setStatus("404")
        }
    }
}