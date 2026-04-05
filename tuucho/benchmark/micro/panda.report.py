import glob
import json
import matplotlib
import os
import pandas as pd

matplotlib.use("Agg")
import matplotlib.pyplot as plt
from datetime import datetime
import shutil
from collections import defaultdict

# ------------------------
# Input/output paths
# ------------------------
input_dir = ".validation/benchmark"
reports_dir = "build/reports/benchmark"
graph_dir = os.path.join(reports_dir, "graph")

# Clean output benchmark folder
if os.path.exists(reports_dir):
    shutil.rmtree(reports_dir)
os.makedirs(graph_dir, exist_ok=True)


# ------------------------
# Helpers
# ------------------------
def parse_parts(file_name):
    """
    Extracts versionName and datetime from filename.
    Example: "0.0.1_alpha30_2.3.10-20260404-205119.json"
    Returns: ("0.0.1_alpha30_2.3.10", datetime(...))
    """
    base = os.path.basename(file_name).replace(".json", "")
    # datetime suffix is always 15 chars: -YYYYMMDD-HHMMSS
    if len(base) < 16 or base[-16] != "-":
        return base, datetime.now()
    version = base[:-16]  # everything before -YYYYMMDD-HHMMSS
    dt_str = base[-15:]  # YYYYMMDD-HHMMSS
    dt = datetime.strptime(dt_str, "%Y%m%d-%H%M%S")
    return version, dt


def format_label(row):
    """Return version name only for X-axis labels."""
    return row["version"]


# ------------------------
# Load JSON files
# ------------------------
files = sorted(
    glob.glob(os.path.join(input_dir, "*.json")),
    key=lambda f: parse_parts(f)[1],  # sort by datetime
    reverse=False  # oldest first
)

all_data = []

for f in files:
    version, dt = parse_parts(f)
    with open(f) as jf:
        benchmarks = json.load(jf)
        for bench in benchmarks:
            full_name = bench["benchmark"]
            parts = full_name.split(".")
            class_name = parts[-2]
            method_name = parts[-1]

            params = bench.get("params", {})
            param_str = ",".join(f"{k}={v}" for k, v in params.items()) if params else "default"

            metric = bench["primaryMetric"]

            all_data.append({
                "file": os.path.basename(f),
                "datetime": dt,
                "version": version,  # cleaned version only
                "class": class_name,
                "method": method_name,
                "params": param_str,
                "score": metric["score"],
                "scoreError": metric["scoreError"],
                "low": metric["scoreConfidence"][0],
                "high": metric["scoreConfidence"][1],
                "unit": metric["scoreUnit"]
            })

df = pd.DataFrame(all_data)
if df.empty:
    raise Exception(f"No benchmark data found in {input_dir}")

# Sort by datetime ascending (oldest → latest)
df = df.sort_values("datetime", ascending=True)
html_index = defaultdict(list)

# ------------------------
# Generate graphs
# ------------------------
for (class_name, method_name), group_df in df.groupby(["class", "method"]):
    class_dir = os.path.join(graph_dir, class_name)
    os.makedirs(class_dir, exist_ok=True)

    unit = group_df["unit"].iloc[0]
    plt.figure(figsize=(12, 6))

    for params, subset in group_df.groupby("params"):
        subset = subset.sort_values("datetime", ascending=True)
        x_labels = [format_label(row) for _, row in subset.iterrows()]
        x_positions = range(len(x_labels))

        plt.plot(x_positions, subset["score"], marker="o", label=params)
        plt.fill_between(x_positions, subset["low"], subset["high"], alpha=0.2)

    plt.xticks(range(len(x_labels)), x_labels, rotation=45)
    plt.ylabel(unit)
    plt.xlabel("Version")
    plt.title(f"{class_name}.{method_name}")
    plt.legend(title="Params")
    plt.tight_layout()

    filename = f"{method_name}.png"
    filepath = os.path.join(class_dir, filename)
    plt.savefig(filepath)
    plt.close()

    html_index[class_name].append((method_name, filepath, group_df))
    print(f"✅ Saved: {filepath}")

# ------------------------
# Generate HTML report
# ------------------------
html_file = os.path.join(reports_dir, "index.html")

with open(html_file, "w") as f:
    f.write("""
<html>
<head>
<title>Benchmark Report</title>
<style>
    body { background-color: #121212; color: #e0e0e0; font-family: Arial, sans-serif; margin: 20px; }
    h1 { color: #ffffff; border-bottom: 2px solid #333; padding-bottom: 10px; }
    h2 { margin-top: 40px; color: #90caf9; border-bottom: 1px solid #333; padding-bottom: 5px; }
    h3 { margin-top: 20px; color: #ce93d8; }
    img { margin-top: 10px; margin-bottom: 10px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.8); }
    details { margin-bottom: 20px; }
    summary { cursor: pointer; font-weight: bold; margin-bottom: 5px; }
    table { border-collapse: collapse; width: 100%; margin-top: 5px; }
    th, td { border: 1px solid #333; padding: 4px 8px; text-align: left; }
    th { background-color: #1e1e1e; }
</style>
</head>
<body>
<h1>Benchmark Report</h1>
""")

    for class_name in sorted(html_index.keys()):
        f.write(f"<h2>{class_name}</h2>")

        for method_name, img_path, method_df in sorted(html_index[class_name]):
            rel_img = os.path.relpath(img_path, reports_dir)
            f.write(f"<h3>{method_name}</h3>")
            f.write(f'<img src="{rel_img}" style="max-width:100%;"><br>')

            for params, subset in method_df.groupby("params"):
                f.write(f"<details><summary>Show Data Table (Params: {params})</summary><table>")
                f.write(
                    "<tr><th>Run</th><th>Version</th><th>Score</th><th>ScoreError</th><th>Low</th><th>High</th><th>Unit</th></tr>\n")
                for _, row in subset.sort_values("datetime", ascending=True).iterrows():
                    label = format_label(row)
                    f.write(
                        f"<tr><td>{row['file']}</td><td>{label}</td><td>{row['score']:.12g}</td><td>{row['scoreError']:.12g}</td>"
                        f"<td>{row['low']:.12g}</td><td>{row['high']:.12g}</td><td>{row['unit']}</td></tr>\n")
                f.write("</table></details>")

    f.write("</body></html>")

print(f"🌐 HTML report generated: {html_file}")
