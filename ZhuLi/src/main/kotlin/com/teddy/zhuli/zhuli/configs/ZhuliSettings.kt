package com.teddy.zhuli.zhuli.configs

import com.teddy.zhuli.zhuli.be.BackendType

data class ZhuliSettings(
    val backendType: BackendType,
    val ollamaUrl: String,
    val ollamaModel: String,
    val localModelUrl: String,
    val deepseekUrl: String,
    val deepseekModel: String,
    val deepseekApiKey: String
) {
    companion object {
        fun get(): ZhuliSettings {
            val s = ZhuliSettingsState.getInstance().state
            return ZhuliSettings(
                backendType = s.backendType,
                ollamaUrl = s.ollamaUrl,
                ollamaModel = s.ollamaModel,
                localModelUrl = s.localModelUrl,
                deepseekUrl = s.deepseekUrl,
                deepseekModel = s.deepseekModel,
                deepseekApiKey = s.deepseekApiKey
            )
        }
    }
}
