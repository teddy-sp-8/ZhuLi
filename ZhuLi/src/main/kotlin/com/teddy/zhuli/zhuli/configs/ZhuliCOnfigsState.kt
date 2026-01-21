package com.teddy.zhuli.zhuli.configs

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.teddy.zhuli.zhuli.be.BackendType

@State(name = "ZhuliSettingsState", storages = [Storage("zhuli.xml")])
class ZhuliSettingsState : PersistentStateComponent<ZhuliSettingsState.StateData> {

    data class StateData(
        var backendType: BackendType = BackendType.OLLAMA_PRETRAINED,
        var ollamaUrl: String = "http://127.0.0.1:11434",
        var ollamaModel: String = "qwen2.5-coder:3b-instruct",
        var localModelUrl: String = "http://127.0.0.1:8000",
        var deepseekUrl: String = "https://api.deepseek.com/v1",
        var deepseekModel: String = "deepseek-chat",
        var deepseekApiKey: String = ""
    )

    private var data = StateData()

    override fun getState(): StateData = data
    override fun loadState(state: StateData) { data = state }

    companion object {
        @Volatile
        private var defaultInstance: ZhuliSettingsState? = null
        
        fun getInstance(): ZhuliSettingsState {
            val application = ApplicationManager.getApplication()
            
            // Try to get the service - it should be available if the application is initialized
            val service = try {
                application.getService(ZhuliSettingsState::class.java)
            } catch (e: Exception) {
                null
            }
            
            if (service != null) {
                return service
            }
            
            // Fallback: create a default instance if service is not available
            // This can happen during early initialization or if service registration failed
            synchronized(ZhuliSettingsState::class.java) {
                if (defaultInstance == null) {
                    defaultInstance = ZhuliSettingsState()
                }
                return defaultInstance!!
            }
        }
    }
}
