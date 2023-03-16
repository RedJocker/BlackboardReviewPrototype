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
import org.mindrot.jbcrypt.BCrypt
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

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
            val rawPass = passEt.text.toString()
            val rawPassBytes = rawPass.toByteArray(StandardCharsets.UTF_8)
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val sha256HashPass = messageDigest.digest(rawPassBytes)
            val base64sha256HashPass = Base64.getEncoder().encodeToString(sha256HashPass)
            val bcryptHashPass = BCrypt.hashpw(base64sha256HashPass, BCrypt.gensalt(4))

            call = client.newCall(simpleTestPostRequest("login/", bcryptHashPass))
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