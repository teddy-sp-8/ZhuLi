from itertools import chain
from datasets import load_dataset
from transformers import (
    GPT2Config, GPT2LMHeadModel,
    GPT2TokenizerFast,
    DataCollatorForLanguageModeling,
    Trainer, TrainingArguments
)

MODEL_OUT = "out/kotlin_scratch_model"

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

def group_texts(examples):
    block_size = 256
    concatenated = {k: list(chain(*examples[k])) for k in examples.keys()}
    total_length = len(concatenated["input_ids"])
    total_length = (total_length // block_size) * block_size
    if total_length == 0:
        return {k: [] for k in examples.keys()}
    result = {
        k: [t[i : i + block_size] for i in range(0, total_length, block_size)]
        for k, t in concatenated.items()
    }
    result["labels"] = result["input_ids"].copy()
    return result

tok_ds = ds.map(tok_fn, batched=True, remove_columns=["text"])
tok_ds = tok_ds.map(group_texts, batched=True)

collator = DataCollatorForLanguageModeling(tokenizer=tokenizer, mlm=False)

config = GPT2Config(
    vocab_size=len(tokenizer),
    n_positions=256,
    n_ctx=256,
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
    logging_steps=50,
    learning_rate=5e-4,
    report_to="none",
)

trainer = Trainer(
    model=model,
    args=args,
    train_dataset=tok_ds["train"],
    eval_dataset=tok_ds["validation"],
    data_collator=collator,

)

trainer.train()
trainer.save_model(MODEL_OUT + "/final")
tokenizer.save_pretrained(MODEL_OUT + "/final")
print("Saved model to:", MODEL_OUT + "/final")
