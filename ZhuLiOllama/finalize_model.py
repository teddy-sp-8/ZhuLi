import os
import re
from pathlib import Path
from transformers import GPT2LMHeadModel, GPT2TokenizerFast

DEFAULT_ROOT = Path("out/kotlin_scratch_model")
TOKENIZER_DIR = Path("out/tokenizer") 
FINAL_DIR = Path("out/kotlin_scratch_model/final")


def latest_checkpoint(root: Path) -> Path | None:
    pattern = re.compile(r"checkpoint-(\d+)$")
    checkpoints = []
    for p in root.glob("checkpoint-*"):
        m = pattern.search(p.name)
        if m:
            checkpoints.append((int(m.group(1)), p))
    if not checkpoints:
        return None
    return sorted(checkpoints, key=lambda x: x[0])[-1][1]


def resolve_checkpoint() -> Path:
    env_path = os.getenv("CHECKPOINT_DIR")
    if env_path:
        return Path(env_path)
    candidate = latest_checkpoint(DEFAULT_ROOT)
    if candidate:
        return candidate
    raise FileNotFoundError("No checkpoint found under out/kotlin_scratch_model and CHECKPOINT_DIR not set")


def main() -> None:
    checkpoint_dir = resolve_checkpoint()
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

    model = GPT2LMHeadModel.from_pretrained(str(checkpoint_dir))
    model.resize_token_embeddings(len(tokenizer))

    model.save_pretrained(str(FINAL_DIR))
    tokenizer.save_pretrained(str(FINAL_DIR))

    print(f"Saved final model from {checkpoint_dir} to: {FINAL_DIR.resolve()}")


if __name__ == "__main__":
    main()
