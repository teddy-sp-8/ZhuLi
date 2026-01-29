from pathlib import Path
from transformers import GPT2LMHeadModel, GPT2TokenizerFast

MODEL_OUT = Path("out/kotlin_scratch_model")
TOKENIZER_DIR = Path("out/tokenizer")

checkpoints = sorted(MODEL_OUT.glob("checkpoint-*"), key=lambda p: int(p.name.split("-")[-1]))
if not checkpoints:
    raise SystemExit("No checkpoints found in out/kotlin_scratch_model")

CHECKPOINT_DIR = checkpoints[-1]  # latest
FINAL_DIR = MODEL_OUT / "final_from_checkpoint"

FINAL_DIR.mkdir(parents=True, exist_ok=True)

tokenizer = GPT2TokenizerFast(
    vocab_file=str(TOKENIZER_DIR / "vocab.json"),
    merges_file=str(TOKENIZER_DIR / "merges.txt"),
    bos_token="<s>",
    eos_token="</s>",
    unk_token="<unk>",
    pad_token="<pad>",
)
tokenizer.add_special_tokens({"additional_special_tokens": ["<file>", "</file>"]})

model = GPT2LMHeadModel.from_pretrained(str(CHECKPOINT_DIR))
model.resize_token_embeddings(len(tokenizer))

model.save_pretrained(str(FINAL_DIR))
tokenizer.save_pretrained(str(FINAL_DIR))

print("Latest checkpoint:", CHECKPOINT_DIR)
print("Saved final model to:", FINAL_DIR.resolve())
