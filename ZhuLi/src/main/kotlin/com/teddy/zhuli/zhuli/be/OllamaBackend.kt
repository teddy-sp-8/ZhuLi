package com.teddy.zhuli.zhuli.be

import com.teddy.zhuli.zhuli.util.HttpJson
import org.json.JSONObject

class OllamaBackend(
    private val url: String,
    private val model: String
) : Backend {

    override val name: String = "Local (Ollama)"

    override fun generate(prompt: String): String {
        val payload = JSONObject()
            .put("model", model)
            .put("prompt", prompt)
            .put("stream", false)
            .toString()

        val body = HttpJson.post(url, payload)
        val json = JSONObject(body)
        return json.optString("response", body)
    }
}
