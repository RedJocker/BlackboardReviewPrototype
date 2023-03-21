package org.hyperskill.blackboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hyperskill.blackboard.network.BaseClient

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("onCreate")
        BaseClient.baseurl = intent.extras?.getString("baseUrl") ?: BaseClient.baseurl
        println("baseUrl : ${BaseClient.baseurl}")

    }
}