package com.teddy.zhuli.zhuli.configs

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.teddy.zhuli.zhuli.be.BackendType

@Service(Service.Level.APP)
@State(
    name = "ZhuliSettings",
    storages = [Storage("zhuli.xml")]
)
class ZhuliSettingsState : PersistentStateComponent<ZhuliSettingsState.StateData> {

    data class StateData(
        var backendType: BackendType = BackendType.OLLAMA,

        var ollamaUrl: String = "http://localhost:11434/api/generate",
        var ollamaModel: String = "qwen2.5-coder:3b-instruct",

        var pythonUrl: String = "http://127.0.0.1:8000/generate",

        var remoteApiUrl: String = "",
        var remoteApiKey: String = "",
        var remoteModel: String = "deepseek"
    )

    private var state = StateData()

    override fun getState(): StateData = state

    override fun loadState(state: StateData) {
        this.state = state
    }

    companion object {
        fun get(): StateData = service<ZhuliSettingsState>().state
    }
}
