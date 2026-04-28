package com.sanctra.feature.net

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class OrchestratorApi(
    private val baseUrl: String,
    private val client: OkHttpClient = OkHttpClient()
) {
  fun startSession(personId: String): Result<String> {
    val body = JSONObject(mapOf("person_id" to personId)).toString()
    val req = Request.Builder()
      .url("$baseUrl/session/start")
      .post(RequestBody.create(MediaType.get("application/json"), body))
      .build()
    client.newCall(req).execute().use { resp ->
      if (!resp.isSuccessful) return Result.failure(IllegalStateException("HTTP ${resp.code()}"))
      val sid = JSONObject(resp.body()!!.string()).getString("session_id")
      return Result.success(sid)
    }
  }
}
