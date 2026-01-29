from transformers import GPT2LMHeadModel, GPT2TokenizerFast
from pathlib import Path
import torch

FINAL_DIR = Path("out/kotlin_scratch_model/final")

tokenizer = GPT2TokenizerFast.from_pretrained(str(FINAL_DIR))
model = GPT2LMHeadModel.from_pretrained(str(FINAL_DIR))
model.eval()

prompt = "power math"
inputs = tokenizer(prompt, return_tensors="pt")

with torch.no_grad():
    out = model.generate(
        **inputs,
        max_new_tokens=120,
        do_sample=True,
        temperature=0.9,
        top_p=0.95,
        pad_token_id=tokenizer.eos_token_id,
    )

print(tokenizer.decode(out[0], skip_special_tokens=False))
