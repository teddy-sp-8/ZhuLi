from http.client import responses

import numpy as np
import pandas as pd
import kagglehub
import os
import requests
from pathlib import Path
origin_path = ""
path = kagglehub.dataset_download("joonasyoon/file-format-detection") +"\\dataset.csv"
print("Path to dataset files:", path)
df =pd.read_csv(path)

filtered_data = df[df["language"].isin(["Kotlin"]) ].head(30)

for items in filtered_data["file_path"][1:]:

    file_path = Path(origin_path)/items
    with open(file_path,"r") as f:
        lines = f.readlines()
        print(lines)
