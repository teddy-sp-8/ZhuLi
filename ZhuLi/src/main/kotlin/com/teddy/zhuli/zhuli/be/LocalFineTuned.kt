package com.teddy.zhuli.zhuli.be

import com.teddy.zhuli.zhuli.util.HttpJson
import org.json.JSONObject

class LocalFineTunedBackend(
    private val url: String
) : Backend {

    override val name: String = "Local Fine-Tuned (Python Server)"

    override fun generate(prompt: String): String {
        val payload = JSONObject()
            .put("prompt", prompt)
            .put("max_new_tokens", 180)
            .put("temperature", 0.8)
            .put("top_p", 0.95)
            .toString()

        val body = HttpJson.post("$url/generate", payload)

        val json = JSONObject(body)
        return json.getString("code")
    }
}
