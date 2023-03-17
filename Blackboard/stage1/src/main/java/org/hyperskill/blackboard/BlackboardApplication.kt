package org.hyperskill.blackboard

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class BlackboardApplication: Application() {

    val blackboardClient by lazy {
        BlackBoardClient(moshi)
    }

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}