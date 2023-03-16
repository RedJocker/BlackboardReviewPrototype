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


    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = BlackBoardMockBackEnd()
        baseUrlMockWebServer = mockWebServer.url("/").toString()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testSomething() {

        val arg = Intent().apply {
            putExtra("baseUrl", baseUrlMockWebServer)
        }


        testActivity(arguments = arg) {
            inputEt.setText("1234")
            submitBtn.clickAndRun()

            val request = mockWebServer.takeRequest()
            assertEquals("Wrong request method", "POST", request.method)
            assertEquals("Wrong request path", "/login/", request.path)

            println("before sleep")
            Thread.sleep(100) // Dispatcher.dispatch and Callback.onResponse are async
            println("after sleep")
            shadowLooper.runToEndOfTasks()  // runOnUiThread goes to Handler queue
            println("after runToEndOfTasks")
            assertEquals("Wrong text", "{\"token\": \"abc\"}", helloTv.text.toString())
        }
    }
}