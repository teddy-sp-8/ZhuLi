from pathlib import Path
import shutil

REPO_PATH = Path(r"") 
OUT_PATH = Path(r"")

if not REPO_PATH.exists():
    raise RuntimeError(f"Repo path does not exist: {REPO_PATH}")

OUT_PATH.mkdir(parents=True, exist_ok=True)

SKIP_DIRS = {
    ".git", "build", "out", ".gradle", ".idea",
    "node_modules", "dist", "tmp", "testData"
}

def should_skip(path: Path) -> bool:
    return any(part in SKIP_DIRS for part in path.parts)

count = 0

for file in REPO_PATH.rglob("*"):
    if should_skip(file):
        continue
    if file.suffix in (".kt", ".kts") and file.is_file():
        safe_name = "__".join(file.parts[-6:])
        target = OUT_PATH / safe_name

        if not target.exists():
            shutil.copy2(file, target)
            count += 1

        if count % 500 == 0:
            print(f"Copied {count} files...")

print("DONE")
print("Total Kotlin files copied:", count)
print("Destination:", OUT_PATH)
