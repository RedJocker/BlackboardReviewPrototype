package org.hyperskill.blackboard.internals.screen

import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.blackboard.Stage1UnitTest

class StudentScreen(private val test : Stage1UnitTest) {

    init {
        with(test) {
            activity.findViewByString<TextView>("gradesLabel")
            activity.findViewByString<TextView>("examLabel")
        }
    }

    val studentNameTV: TextView = with(test) {
        activity.findViewByString("studentNameTV")
    }

    val gradesRV: RecyclerView = with(test) {
        activity.findViewByString("gradesRV")
    }

    val partialResultTV: TextView = with(test) {
        activity.findViewByString("partialResultTV")
    }

    val examET: EditText = with(test) {
        activity.findViewByString("examET")
    }

    val finalResultTV: TextView = with(test) {
        activity.findViewByString("finalResultTV")
    }
}