package com.teddy.zhuli.zhuli.be

object ResponseCleaner {
    fun extractKotlin(text: String): String {
        val start = text.indexOf("```")
        if (start != -1) {
            val end = text.indexOf("```", start + 3)
            if (end != -1) {
                val inside = text.substring(start + 3, end)
                return inside.removePrefix("kotlin").trim()
            }
        }
        return text.trim()
    }
}
