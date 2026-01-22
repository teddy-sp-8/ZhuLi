package com.teddy.zhuli.zhuli.util

object CodeExtract {
    fun extractKotlinCode(text: String): String {
        val fenceStart = text.indexOf("```")
        if (fenceStart != -1) {
            val fenceEnd = text.indexOf("```", fenceStart + 3)
            if (fenceEnd != -1) {
                val inside = text.substring(fenceStart + 3, fenceEnd)
                return inside.removePrefix("kotlin").trim()
            }
        }
        return text.trim()
    }
}
