package com.teddy.zhuli.zhuli.configs

import com.teddy.zhuli.zhuli.be.BackendType

data class ZhuliSettings(
    val backendType: BackendType,
    val ollamaUrl: String,
    val ollamaModel: String,
    val pythonUrl: String,
    val remoteApiUrl: String,
    val remoteApiKey: String,
    val remoteModel: String
) {
    companion object {
        fun get(): ZhuliSettings {
            val s = ZhuliSettingsState.get()
            return ZhuliSettings(
                backendType = s.backendType,
                ollamaUrl = s.ollamaUrl,
                ollamaModel = s.ollamaModel,
                pythonUrl = s.pythonUrl,
                remoteApiUrl = s.remoteApiUrl,
                remoteApiKey = s.remoteApiKey,
                remoteModel = s.remoteModel
            )
        }
    }
}
