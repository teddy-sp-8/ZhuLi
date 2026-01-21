package com.teddy.zhuli.zhuli.be

import com.teddy.zhuli.zhuli.be.Backend
import com.teddy.zhuli.zhuli.util.HttpJson
import org.json.JSONObject

class Ollama(
    private val baseUrl: String,
    private val model: String
) : Backend {

    override val name: String = "Local Pretrained (Ollama)"

    override fun generate(prompt: String): String {
        val payload = JSONObject()
            .put("model", model)
            .put("prompt", prompt)
            .put("stream", false)
            .toString()

        val body = HttpJson.postJson("$baseUrl/api/generate", payload)

        val json = JSONObject(body)
        return if (json.has("response")) json.getString("response") else body
    }
}
