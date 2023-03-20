package org.hyperskill.blackboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("onCreate")
        (application as BlackboardApplication).loginClient.apply {
            baseurl = intent.extras?.getString("baseUrl") ?: baseurl
            println("baseUrl : $baseurl")
        }
    }
}