package org.hyperskill.blackboard

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.MockWebServer
import org.hyperskill.blackboard.internals.AbstractUnitTest
import org.hyperskill.blackboard.internals.backend.BlackBoardMockBackEnd
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase
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
        val view = activity.findViewByString<Button>("loginBtn")
        view
    }

    private val usernameEt: EditText by lazy {
        activity.findViewByString("loginUsernameEt")
    }

    private val passEt: EditText by lazy {
        activity.findViewByString("loginPassEt")
    }

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    lateinit var mockWebServer: MockWebServer
    lateinit var baseUrlMockWebServer: String
    lateinit var blackBoardMockBackEnd: BlackBoardMockBackEnd


    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        blackBoardMockBackEnd = BlackBoardMockBackEnd(moshi)
        mockWebServer.dispatcher = blackBoardMockBackEnd
        baseUrlMockWebServer = mockWebServer.url("/").toString()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testCorrectUsernamePasswordProduces200OkOnBackendResponse() {

        val arg = Intent().apply {
            putExtra("baseUrl", baseUrlMockWebServer)
        }


        testActivity(arguments = arg) {
            val user = MockUserDatabase.users["George"]!!

            usernameEt.setText(user.userName)
            passEt.setText(user.plainPass)
            submitBtn.clickAndRun()

            val request = mockWebServer.takeRequest()
            assertEquals("Wrong request method", "POST", request.method)
            assertEquals("Wrong request path", "/login", request.path)

            val response = blackBoardMockBackEnd.poolResponse()

            if(response.status != "HTTP/1.1 200 OK") {
                throw AssertionError(
                    "Wrong status '${response.status}' with body '${response.getBody()?.readUtf8()}'"
                )
            } else {
                Thread.sleep(50)           // Callback.onResponse is async
                shadowLooper.runToEndOfTasks()  // runOnUiThread goes to Handler queue
                println("after runToEndOfTasks")

                val responseString = helloTv.text.toString()
                println(responseString)

                val messageErrorLogin = "Wrong login response"
                val expectedLoginResponse = "Success(username=George, token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJST0xFIjoiVEVBQ0hFUiIsInN1YiI6Ikdlb3JnZSIsImlzcyI6ImJsYWNrQm9hcmRBcHAifQ.hY4fC9rkQniZMmSIREK9esqUpxK187gkEgJl4pgt_iA, role=TEACHER)"
                assertEquals(messageErrorLogin, expectedLoginResponse, responseString)
            }
        }
    }
}