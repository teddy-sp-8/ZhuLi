package com.teddy.zhuli.zhuli.be

import com.teddy.zhuli.zhuli.util.HttpJson
import org.json.JSONObject

class RemoteApiBackend(
    private val url: String,
    private val apiKey: String,
    private val model: String
) : Backend {

    override val name: String = "Remote API ($model)"

    override fun generate(prompt: String): String {
        if (url.isBlank()) throw IllegalStateException("Remote API URL is empty")
        if (apiKey.isBlank()) throw IllegalStateException("Remote API key is empty")

        val payload = JSONObject()
            .put("model", model)
            .put("prompt", prompt)
            .toString()

        val body = HttpJson.post(
            url = url,
            jsonBody = payload,
            headers = mapOf("Authorization" to "Bearer $apiKey")
        )

        val json = JSONObject(body)
        return json.optString("text", body)
    }
}
