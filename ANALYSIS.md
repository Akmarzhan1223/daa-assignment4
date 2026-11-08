# Assignment 4 — analysis (complete in English)

## Environment and commands

Selected language/runtime version; OS/CPU/RAM; build/run/test commands. Record the final commit URL in Moodle. Report actual measurements only.

## Algorithm explanations

For each of lcs, merge, tree, naive, kmp, rk, suffix and aho:

- State or invariant; meaning of each index.
- Transition/update and base cases.
- Processing order and why dependencies are ready.
- Correctness argument.
- Reconstruction and tie handling, where applicable.
- Time and memory; include input/output size and actual sorting/container choices.
- One boundary/counterexample test and what defect it detects.

For suffix discuss doubling and Kasai separately. For rk use base 31/mod 101 and character verification; state the candidate-sensitive bound. For aho explain suffix-pattern counts, duplicates and reset.

## Own tests

|Command|Input or fixture path|Expected result|Actual result / log|What it checks|
|---|---|---|---|---|

## Search comparison

Use the same inputs for naive/KMP/RK. At three increasing sizes in the common domain (n, m≤3000), measure random and repetitive cases separately. Record generator/seed, n, m, pattern distribution and correctness verification first. At least three measured runs per algorithm/size/case; report raw times, unit and median. Exclude compilation; give the identical timing scope, warm-up/JIT policy and commands.

|Case|n|m|Seed/generator|Algorithm|Run 1|Run 2|Run 3|Median|Correct outputs checked|
|---|---:|---:|---|---|---:|---:|---:|---:|---|

Explain observations without treating measured times as a proof of asymptotic complexity. Discuss how the deliberately small modulus affects RK; do not generalize it to every production hash implementation. Add the known collision case as a correctness test.

## Sources and AI assistance

I used an AI assistant (DeepSeek) as an educational tool to help clarify the theory behind the algorithms and debug specific Java syntax or logic errors. I was responsible for:
- Structuring the project, setting up the environment, and integrating the code into the provided template.
- Reviewing, modifying, and ensuring the correctness of the DP transitions and string matching logic.
- Iteratively running the test suite (`python tools/run.py --check`), diagnosing failures, and adjusting the code to pass all 44 public test cases and the state reset checks.
- Designing edge cases (empty strings, overlaps, negative weights) to verify the robustness of the final implementation.
