package com.teddy.zhuli.zhuli.be

import com.teddy.zhuli.zhuli.util.HttpJson
import org.json.JSONObject

class LocalPythonBackend(
    private val url: String,
    private val mode: String
) : Backend {

    override val name: String = "Local Python ($mode)"

    override fun generate(prompt: String): String {
        val payload = JSONObject()
            .put("mode", mode)
            .put("prompt", prompt)
            .toString()

        val body = HttpJson.post(url, payload)
        val json = JSONObject(body)

        return json.optString("text", body)
    }
}
