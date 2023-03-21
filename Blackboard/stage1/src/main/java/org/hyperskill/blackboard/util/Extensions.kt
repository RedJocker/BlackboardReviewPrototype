package org.hyperskill.blackboard.util

import android.content.Context
import android.widget.Toast

object Extensions {

    fun Context.showToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.show()
    }
}