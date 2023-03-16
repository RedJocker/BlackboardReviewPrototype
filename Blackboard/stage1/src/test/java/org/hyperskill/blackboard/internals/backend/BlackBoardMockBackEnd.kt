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

    val resposeList = mutableListOf<MockResponse>()

    override fun dispatch(request: RecordedRequest): MockResponse {
        println("dispatch $request")
        return when (request.path) {
            "/login/" , "/login" -> {
                loginService.serve(request)
                    .also { resposeList.add(it) }
            }
            else -> {
                println("mock 404")
                MockResponse()
                    .setStatus("404")
                    .also { resposeList.add(it) }
            }
        }
    }

    fun poolResponse() : MockResponse{
        var response = resposeList.firstOrNull()
        var tries = 0
        while(response == null) {
            Thread.sleep(20)
            response = resposeList.firstOrNull()
            tries++
            if(tries > 15) {
                throw AssertionError("Test was not able to retrieve a response in time")
            }
        }
        println("pooling tries $tries")
        return response.also { resposeList.removeAt(0) }
    }

    @Suppress("UNUSED")
    fun clearResponseList() {
        resposeList.clear()
    }
}