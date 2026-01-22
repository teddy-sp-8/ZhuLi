package com.teddy.zhuli.zhuli.be


interface Backend {
    val name: String
    fun generate(prompt: String): String
}
