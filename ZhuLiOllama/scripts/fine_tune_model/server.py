import os
from pathlib import Path
from fastapi import FastAPI
from pydantic import BaseModel
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM

MODEL_DIR = Path(os.getenv("ZHULI_MODEL_DIR", "ml/out/kotlin_finetuned_merged")).resolve()
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

app = FastAPI()

class GenerateRequest(BaseModel):
    prompt: str
    max_new_tokens: int = 180
    temperature: float = 0.8
    top_p: float = 0.95

class GenerateResponse(BaseModel):
    code: str

tok = AutoTokenizer.from_pretrained(str(MODEL_DIR), use_fast=True)
if tok.pad_token is None:
    tok.pad_token = tok.eos_token

model = AutoModelForCausalLM.from_pretrained(str(MODEL_DIR), torch_dtype="auto")
model.to(DEVICE)
model.eval()

def clean(text: str) -> str:
    s = text.find("```")
    if s != -1:
        e = text.find("```", s + 3)
        if e != -1:
            inside = text[s + 3:e]
            return inside.removeprefix("kotlin").strip()
    return text.strip()

@app.post("/generate", response_model=GenerateResponse)
def generate(req: GenerateRequest):
    inputs = tok(req.prompt, return_tensors="pt").to(DEVICE)
    with torch.no_grad():
        out = model.generate(
            **inputs,
            max_new_tokens=req.max_new_tokens,
            do_sample=True,
            temperature=req.temperature,
            top_p=req.top_p,
            pad_token_id=tok.eos_token_id,
            eos_token_id=tok.eos_token_id,
        )
    text = tok.decode(out[0], skip_special_tokens=False)
    return GenerateResponse(code=clean(text))
