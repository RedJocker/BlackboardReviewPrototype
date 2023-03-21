package org.hyperskill.blackboard

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.hyperskill.blackboard.network.login.LoginClient

class BlackboardApplication: Application() {

    val loginClient by lazy {
        LoginClient(okHttpClient, moshi)
    }

    private val okHttpClient by lazy {
        OkHttpClient()
    }

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}