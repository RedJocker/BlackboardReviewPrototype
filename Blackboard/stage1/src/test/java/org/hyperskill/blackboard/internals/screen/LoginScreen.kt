package org.hyperskill.blackboard.internals.screen

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.hyperskill.blackboard.Stage1UnitTest

class LoginScreen(private val test : Stage1UnitTest) {

    val helloTv: TextView = with(test) {
        activity.findViewByString<TextView>("helloTv")
    }

    val submitBtn: Button = with(test) {
        activity.findViewByString<Button>("loginBtn")
    }

    val usernameEt: EditText = with(test) {
        activity.findViewByString("loginUsernameEt")
    }

    val passEt: EditText = with(test) {
        activity.findViewByString("loginPassEt")
    }
}