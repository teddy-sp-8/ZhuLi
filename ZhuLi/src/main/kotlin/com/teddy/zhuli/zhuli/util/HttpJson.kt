package com.teddy.zhuli.zhuli.util

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object HttpJson {
    private val client = HttpClient.newHttpClient()

    fun postJson(url: String, json: String, headers: Map<String, String> = emptyMap()): String {
        val builder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.ofString(json))

        builder.header("Content-Type", "application/json")
        headers.forEach { (k, v) -> builder.header(k, v) }

        val req = builder.build()
        val resp = client.send(req, HttpResponse.BodyHandlers.ofString())
        if (resp.statusCode() !in 200..299) {
            throw RuntimeException("HTTP ${resp.statusCode()} from $url\n${resp.body()}")
        }
        return resp.body()
    }
}
