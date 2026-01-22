package com.teddy.zhuli.zhuli.be

import com.teddy.zhuli.zhuli.configs.ZhuliSettingsState

object BackendFactory {
    fun create(type: BackendType, s: ZhuliSettingsState.StateData): Backend {
        return when (type) {
            BackendType.OLLAMA -> OllamaBackend(s.ollamaUrl, s.ollamaModel)
            BackendType.PYTHON_SCRATCH -> LocalPythonBackend(s.pythonUrl, "scratch")
            BackendType.PYTHON_LORA -> LocalPythonBackend(s.pythonUrl, "lora")
            BackendType.REMOTE_API -> RemoteApiBackend(s.remoteApiUrl, s.remoteApiKey, s.remoteModel)
        }
    }
}
