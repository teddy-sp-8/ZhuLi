from tokenizers import ByteLevelBPETokenizer
from pathlib import Path

Path("out/tokenizer").mkdir(parents=True, exist_ok=True)

tok = ByteLevelBPETokenizer()
tok.train(
    files=["data/train.txt"],
    vocab_size=12000,
    min_frequency=2,
    special_tokens=["<s>", "</s>", "<pad>", "<unk>", "<file>", "</file>"]
)
tok.save_model("out/tokenizer")
print("Saved tokenizer to out/tokenizer/")
