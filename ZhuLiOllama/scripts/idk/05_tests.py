from pathlib import Path
import torch
from transformers import GPT2LMHeadModel, GPT2TokenizerFast

MODEL_DIR = Path("out/kotlin_scratch_model/final_from_checkpoint")

tokenizer = GPT2TokenizerFast.from_pretrained(str(MODEL_DIR))
model = GPT2LMHeadModel.from_pretrained(str(MODEL_DIR)).eval()

prompt = "<file>\nfun add(a: Int, b: Int): Int {\n"
inputs = tokenizer(prompt, return_tensors="pt")

with torch.no_grad():
    out = model.generate(
        **inputs,
        max_new_tokens=120,
        do_sample=True,
        top_p=0.95,
        temperature=0.8,
        eos_token_id=tokenizer.eos_token_id,
        pad_token_id=tokenizer.eos_token_id
    )

print(tokenizer.decode(out[0], skip_special_tokens=False))
