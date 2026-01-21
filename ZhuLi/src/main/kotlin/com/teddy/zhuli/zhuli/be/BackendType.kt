package com.teddy.zhuli.zhuli.be

enum class BackendType(val displayName: String) {
    OLLAMA_PRETRAINED("Local Pretrained (Ollama)"),
    LOCAL_FINE_TUNED("Local Fine-Tuned (Python Server)"),
    REMOTE_API("Remote API (DeepSeek)")
}
