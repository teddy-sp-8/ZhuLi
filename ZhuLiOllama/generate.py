import torch
from transformers import GPT2LMHeadModel, PreTrainedTokenizerFast

m = "out/kotlin_scratch_model/final"
tokenizer = PreTrainedTokenizerFast.from_pretrained(m)
model = GPT2LMHeadModel.from_pretrained(m).eval()

prompt = "<file>\nfun add(a: Int, b: Int): Int {\n"
inputs = tokenizer(prompt, return_tensors="pt")

with torch.no_grad():
    out = model.generate(
        **inputs,
        max_new_tokens=120,
        do_sample=True,
        top_p=0.95,
        temperature=0.8,
        eos_token_id=tokenizer.eos_token_id
    )

print(tokenizer.decode(out[0]))
