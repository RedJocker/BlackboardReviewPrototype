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

class MainActivity : AppCompatActivity() {

    lateinit var helloTv : TextView
    lateinit var passEt : EditText
    lateinit var button: Button
    lateinit var blackboardClient: BlackBoardClient
    lateinit var call: Call

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        helloTv = findViewById(R.id.helloTv)
        passEt = findViewById(R.id.inputEt)
        button = findViewById(R.id.submitBtn)

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
            call = client.newCall(simpleTestPostRequest("login/", passEt.text.toString()))
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