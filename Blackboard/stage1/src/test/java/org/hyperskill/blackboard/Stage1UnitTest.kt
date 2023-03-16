package org.hyperskill.blackboard

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.mockwebserver.MockWebServer
import org.hyperskill.blackboard.internals.AbstractUnitTest
import org.hyperskill.blackboard.internals.backend.BlackBoardMockBackEnd
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Stage1UnitTest : AbstractUnitTest<MainActivity>(MainActivity::class.java){

    private val helloTv: TextView by lazy {
        val view = activity.findViewByString<TextView>("helloTv")
        view
    }

    private val submitBtn: Button by lazy {
        val view = activity.findViewByString<Button>("submitBtn")
        view
    }

    private val inputEt: EditText by lazy {
        activity.findViewByString("inputEt")
    }

    lateinit var mockWebServer: MockWebServer
    lateinit var baseUrlMockWebServer: String
    lateinit var blackBoardMockBackEnd: BlackBoardMockBackEnd


    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        blackBoardMockBackEnd = BlackBoardMockBackEnd()
        mockWebServer.dispatcher = blackBoardMockBackEnd
        baseUrlMockWebServer = mockWebServer.url("/").toString()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testCorrectPasswordProduces200OkOnBackendResponse() {

        val arg = Intent().apply {
            putExtra("baseUrl", baseUrlMockWebServer)
        }


        testActivity(arguments = arg) {
            inputEt.setText("1234")
            submitBtn.clickAndRun()

            val request = mockWebServer.takeRequest()
            assertEquals("Wrong request method", "POST", request.method)
            assertEquals("Wrong request path", "/login/", request.path)

            val response = blackBoardMockBackEnd.poolResponse()

            if(response.status != "HTTP/1.1 200 OK") {
                throw AssertionError(
                    "Wrong status '${response.status}' with body '${response.getBody()?.readUtf8()}'"
                )
            } else {
                Thread.sleep(50)           // Callback.onResponse is async
                shadowLooper.runToEndOfTasks()  // runOnUiThread goes to Handler queue
                println("after runToEndOfTasks")
                assertEquals("Wrong text", "{\"token\": \"abc\"}", helloTv.text.toString())
            }
        }
    }
}