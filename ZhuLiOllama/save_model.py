import os
import re
from pathlib import Path
from transformers import AutoModelForCausalLM, AutoTokenizer

ROOT = Path("out/kotlin_scratch_model")
DEFAULT_OUT = Path("kotlin-model-final")


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
    candidate = latest_checkpoint(ROOT)
    if candidate:
        return candidate
    raise FileNotFoundError("No checkpoint found under out/kotlin_scratch_model and CHECKPOINT_DIR not set")


def main() -> None:
    checkpoint = resolve_checkpoint()
    final_dir = Path(os.getenv("FINAL_DIR", DEFAULT_OUT))
    final_dir.mkdir(exist_ok=True)

    tokenizer = AutoTokenizer.from_pretrained(checkpoint)
    model = AutoModelForCausalLM.from_pretrained(checkpoint)

    model.save_pretrained(final_dir)
    tokenizer.save_pretrained(final_dir)

    print(f"Saved model from {checkpoint} to {final_dir}")


if __name__ == "__main__":
    main()
