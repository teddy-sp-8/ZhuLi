package com.teddy.zhuli.zhuli.be

import com.teddy.zhuli.zhuli.util.HttpJson
import org.json.JSONArray
import org.json.JSONObject

class RemoteApiBackend(
    private val apiKey: String,
    private val baseUrl: String,
    private val model: String
) : Backend {

    override val name: String = "Remote API (DeepSeek)"

    override fun generate(prompt: String): String {
        if (apiKey.isBlank()) throw IllegalStateException("DeepSeek API key is empty")

        val payload = JSONObject()
            .put("model", model)
            .put("messages", JSONArray()
                .put(JSONObject().put("role", "system").put("content", "Return only Kotlin code. No markdown. No explanation."))
                .put(JSONObject().put("role", "user").put("content", prompt))
            )
            .put("temperature", 0.7)
            .toString()

        val headers = mapOf(
            "Authorization" to "Bearer $apiKey",
            "Content-Type" to "application/json"
        )

        val body = HttpJson.postJson("$baseUrl/chat/completions", payload, headers)

        val json = JSONObject(body)
        val choices = json.getJSONArray("choices")
        if (choices.length() == 0) return ""

        val message = choices.getJSONObject(0).getJSONObject("message")
        return message.getString("content")
    }
}
