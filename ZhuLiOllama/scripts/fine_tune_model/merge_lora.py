import os
from transformers import AutoTokenizer, AutoModelForCausalLM
from peft import PeftModel

BASE_MODEL = os.getenv("BASE_MODEL", "Qwen/Qwen2.5-Coder-1.5B-Instruct")
LORA_DIR = "ml/out/kotlin_lora"
MERGED_OUT = "ml/out/kotlin_finetuned_merged"

def main():
    tok = AutoTokenizer.from_pretrained(BASE_MODEL, use_fast=True)
    if tok.pad_token is None:
        tok.pad_token = tok.eos_token

    base = AutoModelForCausalLM.from_pretrained(BASE_MODEL, device_map="cpu", torch_dtype="auto")
    lora = PeftModel.from_pretrained(base, LORA_DIR)

    merged = lora.merge_and_unload()
    merged.save_pretrained(MERGED_OUT)
    tok.save_pretrained(MERGED_OUT)
    print("Saved merged model to:", MERGED_OUT)

if __name__ == "__main__":
    main()
