package com.teddy.zhuli.zhuli.be


import com.teddy.zhuli.zhuli.configs.ZhuliSettings

object BackendFactory {
    fun create(type: BackendType, settings: ZhuliSettings): Backend {
        return when (type) {
            BackendType.OLLAMA_PRETRAINED ->
                Ollama(settings.ollamaUrl, settings.ollamaModel)

            BackendType.LOCAL_FINE_TUNED ->
                LocalFineTunedBackend(settings.localModelUrl)

            BackendType.REMOTE_API ->
                RemoteApiBackend(settings.deepseekApiKey, settings.deepseekUrl, settings.deepseekModel)
        }
    }
}
