package com.teddy.zhuli.zhuli.be

enum class BackendType(val displayName: String) {
    OLLAMA("Local (Ollama)"),
    PYTHON_SCRATCH("Local Python (Scratch)"),
    PYTHON_LORA("Local Python (LoRA fine-tuned)"),
    REMOTE_API("Remote API")
}
