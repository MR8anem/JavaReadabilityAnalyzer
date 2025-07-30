# JavaReadabilityAnalyzer

## Description

This project provides a command-line tool for analyzing and classifying the readability of Java code snippets. It is designed to preprocess Java snippet files, extract various code metrics, and then use machine learning to classify the readability of these snippets based on human-annotated ground truth data.

The tool supports two main operations:
- **Preprocessing**: Extracts features from a directory of `.jsnp` Java snippet files and combines them with ground truth readability scores to produce a CSV dataset.
- **Classification**: Trains and evaluates a logistic regression model on the preprocessed dataset to predict code readability.

The project is implemented in Java and uses [Picocli](https://picocli.info/) for the CLI, [Weka](https://www.cs.waikato.ac.nz/ml/weka/) for machine learning, and [JavaParser](https://javaparser.org/) for code analysis.

---

## Build Instructions

This project uses Maven for dependency management and building.

```sh
mvn clean package
```

This will produce a runnable JAR file in the `target/` directory.

---

## Usage

The main entry point is `readability.ReadabilityAnalysisMain`, which provides two subcommands: `preprocess` and `classify`.

### 1. Preprocess

Extract features from Java snippet files and combine them with ground truth readability scores.

**Command:**
```sh
java -jar target/Readability-Analysis-1.0.jar preprocess \
  -s <source_dir> \
  -g <ground_truth.csv> \
  -t <output.csv> \
  [featureMetrics...]
```

**Arguments:**
- `-s, --source` — Directory containing `.jsnp` Java snippet files (required)
- `-g, --ground-truth` — CSV file with human readability ratings (required)
- `-t, --target` — Output CSV file for preprocessed data (required, must end with `.csv`)
- `featureMetrics` — One or more feature metrics to extract. Options: `LINES`, `TOKEN_ENTROPY`, `H_VOLUME`, `CYCLOMATIC_COMPLEXITY`

**Example:**
```sh
java -jar target/Readability-Analysis-1.0.jar preprocess \
  -s resources/snippets \
  -g resources/truth_scores.csv \
  -t output.csv \
  LINES TOKEN_ENTROPY H_VOLUME CYCLOMATIC_COMPLEXITY
```

---

### 2. Classify

Train and evaluate a logistic regression model on the preprocessed dataset.

**Command:**
```sh
java -jar target/Readability-Analysis-1.0.jar classify \
  -d <data.csv>
```

**Arguments:**
- `-d, --data` — CSV file produced by the preprocess step (required)

**Example:**
```sh
java -jar target/Readability-Analysis-1.0.jar classify \
  -d output.csv
```

---

## Requirements

- Java 21 or higher
- Maven

---

## Project Structure

- `src/` — Main Java source code
- `resources/` — Example Java snippet files and ground truth data
- `test/` — Unit tests

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**MIT License**

Copyright (c) 2025 MR8anem

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
