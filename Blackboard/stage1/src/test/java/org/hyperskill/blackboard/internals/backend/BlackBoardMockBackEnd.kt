package org.hyperskill.blackboard.internals.backend

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class BlackBoardMockBackEnd : Dispatcher() {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val loginService = LoginService(moshi)

    override fun dispatch(request: RecordedRequest): MockResponse {
        println("dispatch $request")
        return when (request.path) {
            "/login/" , "/login" -> {
                loginService.serve(request)
            }
            else -> {
                println("mock 404")
                MockResponse()
                    .setStatus("404")
            }
        }
    }
}