package org.hyperskill.blackboard

import android.app.Application

class BlackboardApplication: Application() {

    val blackboardClient by lazy {
        BlackBoardClient()
    }
}