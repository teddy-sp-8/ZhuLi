import pandas as pd
import kagglehub
from pathlib import Path
import random

origin_path = Path(r"C:\Users\tedip\.cache\kagglehub\datasets\joonasyoon\file-format-detection\versions\5\dataset")
csv_path = Path(kagglehub.dataset_download("joonasyoon/file-format-detection")) / "dataset.csv"

df = pd.read_csv(csv_path)

kotlin_rows = df[(df["language"] == "Kotlin") & (df["extension"] == "kt")]

print("Kotlin .kt rows:", len(kotlin_rows))

docs = []
for rel in kotlin_rows["file_path"]:
    p = origin_path / rel
    if not p.exists():
        continue

    text = p.read_text(encoding="utf-8", errors="ignore").strip()

    if len(text) < 200:
        continue

    docs.append(f"<file>\n{text}\n</file>")

print("Usable Kotlin files:", len(docs))

random.seed(42)
random.shuffle(docs)

val_n = max(1, int(0.05 * len(docs)))
val_docs = docs[:val_n]
train_docs = docs[val_n:]

out_dir = Path("data")
out_dir.mkdir(exist_ok=True)

(Path("data/train.txt")).write_text("\n\n".join(train_docs), encoding="utf-8")
(Path("data/val.txt")).write_text("\n\n".join(val_docs), encoding="utf-8")

print("Wrote data/train.txt docs:", len(train_docs))
print("Wrote data/val.txt docs:", len(val_docs))
print("Done.")
