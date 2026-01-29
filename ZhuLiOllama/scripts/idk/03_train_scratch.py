from pathlib import Path
from datasets import load_dataset
from transformers import (
    GPT2Config, GPT2LMHeadModel,
    GPT2TokenizerFast,
    DataCollatorForLanguageModeling,
    Trainer, TrainingArguments
)

MODEL_OUT = "out/kotlin_scratch_model"
BLOCK_SIZE = 256

tokenizer = GPT2TokenizerFast(
    vocab_file="out/tokenizer/vocab.json",
    merges_file="out/tokenizer/merges.txt",
    bos_token="<s>",
    eos_token="</s>",
    unk_token="<unk>",
    pad_token="<pad>",
)
tokenizer.add_special_tokens({"additional_special_tokens": ["<file>", "</file>"]})

ds = load_dataset("text", data_files={"train": "data/train.txt", "validation": "data/val.txt"})
ds = ds.filter(lambda x: x["text"] is not None and x["text"].strip() != "")

def tok_fn(batch):
    return tokenizer(batch["text"])

tok_ds = ds.map(tok_fn, batched=True, remove_columns=["text"])

def group_texts(examples):
    concatenated = {k: sum(examples[k], []) for k in examples.keys()}
    total_length = len(concatenated["input_ids"])
    total_length = (total_length // BLOCK_SIZE) * BLOCK_SIZE
    result = {
        k: [t[i : i + BLOCK_SIZE] for i in range(0, total_length, BLOCK_SIZE)]
        for k, t in concatenated.items()
    }
    result["labels"] = result["input_ids"].copy()
    return result

lm_ds = tok_ds.map(group_texts, batched=True)

collator = DataCollatorForLanguageModeling(tokenizer=tokenizer, mlm=False)

config = GPT2Config(
    vocab_size=len(tokenizer),
    n_positions=BLOCK_SIZE,
    n_ctx=BLOCK_SIZE,
    n_embd=256,
    n_layer=4,
    n_head=4,
    bos_token_id=tokenizer.bos_token_id,
    eos_token_id=tokenizer.eos_token_id,
    pad_token_id=tokenizer.pad_token_id,
)

model = GPT2LMHeadModel(config)
model.resize_token_embeddings(len(tokenizer))

args = TrainingArguments(
    output_dir=MODEL_OUT,
    overwrite_output_dir=True,
    num_train_epochs=1,
    per_device_train_batch_size=2,
    per_device_eval_batch_size=2,
    evaluation_strategy="steps",
    eval_steps=200,
    save_steps=200,
    save_total_limit=3,
    logging_steps=50,
    learning_rate=5e-4,
    report_to="none",
)

trainer = Trainer(
    model=model,
    args=args,
    train_dataset=lm_ds["train"],
    eval_dataset=lm_ds["validation"],
    data_collator=collator,
)

last_ckpt = None
if Path(MODEL_OUT).exists():
    checkpoints = sorted(Path(MODEL_OUT).glob("checkpoint-*"), key=lambda p: int(p.name.split("-")[-1]))
    if checkpoints:
        last_ckpt = str(checkpoints[-1])

trainer.train(resume_from_checkpoint=last_ckpt)

trainer.save_model(MODEL_OUT + "/final")
tokenizer.save_pretrained(MODEL_OUT + "/final")
print("Saved model to:", MODEL_OUT + "/final")
