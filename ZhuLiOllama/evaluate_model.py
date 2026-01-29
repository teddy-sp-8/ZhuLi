import json
from pathlib import Path

import torch
from transformers import GPT2LMHeadModel, GPT2TokenizerFast


MODEL_DIR = Path("out/kotlin_scratch_model/final")

PROMPTS = [
    "<file>\nfun add(a: Int, b: Int): Int {\n",
    "<file>\nfun isPrime(n: Int): Boolean {\n",
    "<file>\nclass LruCache<K, V>(private val capacity: Int) {\n",
    "<file>\nfun readLines(path: String): List<String> {\n",
    "<file>\n// Implement binary search\nfun binarySearch(arr: IntArray, target: Int): Int {\n",
]


def generate_one(model, tokenizer, prompt: str, max_new_tokens: int, temperature: float, top_p: float):
    inputs = tokenizer(prompt, return_tensors="pt")
    with torch.no_grad():
        out = model.generate(
            **inputs,
            max_new_tokens=max_new_tokens,
            do_sample=True,
            temperature=temperature,
            top_p=top_p,
            pad_token_id=tokenizer.eos_token_id,
            eos_token_id=tokenizer.eos_token_id,
        )
    text = tokenizer.decode(out[0], skip_special_tokens=False)
    return text


def main():
    if not MODEL_DIR.exists():
        raise FileNotFoundError(
            f"Model dir not found: {MODEL_DIR}. Run finalize_model.py first to export to out/kotlin_scratch_model/final"
        )

    tokenizer = GPT2TokenizerFast.from_pretrained(str(MODEL_DIR))
    model = GPT2LMHeadModel.from_pretrained(str(MODEL_DIR)).eval()

    results = []
    for p in PROMPTS:
        gen = generate_one(model, tokenizer, p, max_new_tokens=180, temperature=0.8, top_p=0.95)
        results.append({"prompt": p, "output": gen})

    Path("out").mkdir(exist_ok=True)
    out_path = Path("out/eval_results.json")
    out_path.write_text(json.dumps(results, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"Wrote {out_path} with {len(results)} generations")


if __name__ == "__main__":
    main()

