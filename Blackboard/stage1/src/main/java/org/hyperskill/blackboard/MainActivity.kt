package org.hyperskill.blackboard

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var helloTv : TextView
    lateinit var passEt : EditText
    lateinit var usernameEt : EditText
    lateinit var button: Button
    lateinit var blackboardClient: BlackBoardClient
    lateinit var call: Call

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        helloTv = findViewById(R.id.helloTv)
        passEt = findViewById(R.id.passEt)
        button = findViewById(R.id.submitBtn)
        usernameEt = findViewById(R.id.usernameEt)

        blackboardClient = (application as BlackboardApplication).blackboardClient.apply {
            baseurl = intent.extras?.getString("baseUrl") ?: baseurl
            println("baseUrl : $baseurl")
        }

        button.setOnClickListener(::onLoginSubmit)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onLoginSubmit(v: View?) {
        println("onLoginSubmit")
        blackboardClient.apply {
            val username = usernameEt.text.toString()
            val rawPass = passEt.text.toString()
            val rawPassBytes = rawPass.toByteArray(StandardCharsets.UTF_8)
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val sha256HashPass = messageDigest.digest(rawPassBytes)
            val base64sha256HashPass = Base64.getEncoder().encodeToString(sha256HashPass)

            call = client.newCall(loginRequest(username, base64sha256HashPass))
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("onFailure")
                    val traceToString = e.stackTraceToString()
                    println(traceToString)

                    runOnUiThread {
                        helloTv.text = traceToString
                    }

                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body!!.string()
                    runOnUiThread {
                        println("onResponse $body")
                        helloTv.text = body
                        //helloTv.text = "{\"tok\": \"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJST0xFIjoiVEVBQ0hFUiIsInN1YiI6Ikdlb3JnZSIsImlzcyI6ImJsYWNrQm9hcmRBcHAifQ.hY4fC9rkQniZMmSIREK9esqUpxK187gkEgJl4pgt_iA\", \"role\": \"TEACHER\", \"extra\": \"hey\"}"
                    }
                }
            })
        }
    }

    override fun onStop() {
        call.cancel()
        super.onStop()
    }
}