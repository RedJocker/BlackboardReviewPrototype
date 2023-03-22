package org.hyperskill.blackboard.network.student

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.network.BaseClient

class StudentClient(client: OkHttpClient, moshi: Moshi): BaseClient(client, moshi) {

    companion object {
        val listIntType = Types.newParameterizedType(
            List::class.java,
            Integer::class.java,
        )
    }

    fun fetchGrades(credential: Credential, callback: Callback): Call {
        return client.newCall(fetchGradesRequest(credential)).also {
            it.enqueue(callback)
        }
    }

    private fun fetchGradesRequest(credential: Credential): Request {
        val url = baseurl.toHttpUrl().resolve("student/${credential.username}/grade")!!
        println(url.toString())
        return Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${credential.token}")
            .build()
    }

    fun parseGrades(body: ResponseBody?): List<Int>? {
        return body?.let {
            moshi.adapter<List<Int>>(listIntType).fromJson(it.string())
        }
    }
}