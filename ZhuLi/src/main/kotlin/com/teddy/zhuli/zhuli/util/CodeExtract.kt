package com.teddy.zhuli.zhuli.util

object CodeExtract {
    fun extractKotlinCode(text: String): String {
        val s = text.indexOf("```")
        if (s != -1) {
            val e = text.indexOf("```", s + 3)
            if (e != -1) {
                val inside = text.substring(s + 3, e)
                return inside.removePrefix("kotlin").trim()
            }
        }
        return text.trim()
    }
}
