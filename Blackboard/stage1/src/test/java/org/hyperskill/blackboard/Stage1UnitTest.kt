package org.hyperskill.blackboard

import android.content.Intent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.MockWebServer
import org.hyperskill.blackboard.internals.AbstractUnitTest
import org.hyperskill.blackboard.internals.backend.BlackBoardMockBackEnd
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase
import org.hyperskill.blackboard.internals.backend.model.Grades
import org.hyperskill.blackboard.internals.backend.model.Student
import org.hyperskill.blackboard.internals.screen.LoginScreen
import org.hyperskill.blackboard.internals.screen.StudentScreen
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Stage1UnitTest : AbstractUnitTest<MainActivity>(MainActivity::class.java){

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
    fun testCorrectStudentLogin() {

        val arg = Intent().apply {
            putExtra("baseUrl", baseUrlMockWebServer)
        }


        testActivity(arguments = arg) {
            val user = (MockUserDatabase.users["Lucas"] as Student).apply {
                grades = Grades(listOf(100, 100, 100, 100), -1)
            }

            LoginScreen(this).apply {
                usernameEt.setText(user.userName)
                passEt.setText(user.plainPass)
                submitBtn.clickAndRun()
            }

            val request = mockWebServer.takeRequest()
            assertEquals("Wrong request method", "POST", request.method)
            assertEquals("Wrong request path", "/login", request.path)

            val loginResponse = blackBoardMockBackEnd.poolResponse()

            if(loginResponse.status != "HTTP/1.1 200 OK") {
                throw AssertionError(
                    "Wrong status '${loginResponse.status}' with body '${loginResponse.getBody()?.readUtf8()}'"
                )
            }

            Thread.sleep(50)           // Callback.onResponse is async
            shadowLooper.runToEndOfTasks()  // runOnUiThread goes to Handler queue
            println("after runToEndOfTasks")

            StudentScreen(this).apply {
                val responseString = studentNameTV.text.toString()
                println(responseString)

                val messageErrorLogin = "Wrong username found on studentNameTv"
                val expectedLoginResponse = "Lucas"
                assertEquals(messageErrorLogin, expectedLoginResponse, responseString)
            }

        }
    }

    @Test
    fun testStudentNetworkFail() {
        val arg = Intent().apply {
            putExtra("baseUrl", baseUrlMockWebServer)
        }


        testActivity(arguments = arg) {
            val user = MockUserDatabase.users["Martin"]!!

            LoginScreen(this).apply {
                usernameEt.setText(user.userName)
                passEt.setText(user.plainPass)
                submitBtn.clickAndRun()
            }


            val request = mockWebServer.takeRequest()
            assertEquals("Wrong request method", "POST", request.method)
            assertEquals("Wrong request path", "/login", request.path)

            val loginResponse = blackBoardMockBackEnd.poolResponse()

            if(loginResponse.status != "HTTP/1.1 200 OK") {
                throw AssertionError(
                    "Wrong status '${loginResponse.status}' with body '${loginResponse.getBody()?.readUtf8()}'"
                )
            }

            Thread.sleep(50)           // Callback.onResponse is async
            shadowLooper.runToEndOfTasks()  // runOnUiThread goes to Handler queue
            println("after runToEndOfTasks")

            val gradesResponse = blackBoardMockBackEnd.poolResponse()
            println(gradesResponse)

            StudentScreen(this).apply {
                val responseString = studentNameTV.text.toString()
                println(responseString)

                val messageErrorLogin = "Wrong username found on studentNameTv"
                val expectedLoginResponse = "Martin"
                assertEquals(messageErrorLogin, expectedLoginResponse, responseString)

                val error = studentNameTV.error.toString()
                val messageErrorMessage = "Expected error message on studentNameTv"
                assertEquals(messageErrorMessage, "Error: 504 Gateway Timeout", error)

                assertTrue(studentNameTV.isFocusable)
                assertTrue(studentNameTV.isFocusableInTouchMode)
                assertTrue(studentNameTV.hasFocus())
            }
        }
    }
}