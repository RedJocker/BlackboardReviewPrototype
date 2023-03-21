package org.hyperskill.blackboard.network.student

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.network.BaseClient

class StudentClient(client: OkHttpClient, moshi: Moshi): BaseClient(client, moshi) {

    fun fetchGrades(credential: Credential) {

    }
}