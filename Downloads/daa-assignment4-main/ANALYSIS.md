# Assignment 4 Analysis

## 1. Longest Common Subsequence (LCS)
**Correctness:** We use a 2D DP table where `dp[i][j]` stores the length of the LCS for prefixes `a[0..i-1]` and `b[0..j-1]`. If characters match, we extend the diagonal. If not, we take the maximum of the top and left cells. Reconstruction traces back from `dp[m][n]`, prepending characters when they match and moving towards the larger adjacent value otherwise. This guarantees an optimal subsequence.
**Complexity:** Time O(|a|*|b|), Space O(|a|*|b|).

## 2. Merge Ordered Blocks (Interval DP)
**Correctness:** `dp[i][j]` is the minimum cost to merge blocks from index `i` to `j` into one. We try every split point `k` (`i <= k < j`). The cost of merging `[i,j]` is the sum of costs for `[i,k]` and `[k+1,j]` plus the sum of sizes in `[i,j]`. We reconstruct the plan by recording the optimal split `k` and recursively outputting the child merges before the parent merge.
**Complexity:** Time O(n^3), Space O(n^2).

## 3. Tree Independent Set
**Correctness:** For each node `u`, we compute `in[u]` (max weight if we take `u`) and `out[u]` (max weight if we skip `u`). If we take `u`, we must skip its children, so `in[u] = weight[u] + sum(out[v])`. If we skip `u`, we can take or skip children, so `out[u] = sum(max(in[v], out[v]))`. We use iterative postorder traversal to avoid stack overflow. Reconstruction goes top-down based on `in` vs `out`.
**Complexity:** Time O(n), Space O(n).

## 4. Search Algorithms (Naive, KMP, Rabin-Karp)
*   **Naive:** Checks every window character by character. Time O(n*m).
*   **KMP:** Builds a prefix function (pi array). It uses this to skip redundant comparisons upon mismatch. Time O(n+m).
*   **Rabin-Karp:** Computes rolling hashes (base 31, mod 101). Equal hashes are verified character by character to avoid collisions. Time O(n+m) on average, O(n*m) worst-case.

**Reproducible Comparison:** On an adversarial input like text `"aaaa...a"` and pattern `"aaa...a"`, Naive takes O(n*m) operations. KMP takes O(n+m) because the prefix function allows immediate fallback. Rabin-Karp computes hashes in O(n+m) but may verify many candidates.

## 5. Suffix Array and Kasai LCP
**Correctness:** The suffix array is built using prefix doubling: we sort suffixes based on pairs of ranks of length 1, then 2, then 4, etc., updating ranks until all are unique. Kasai's algorithm computes the LCP of adjacent suffixes in O(n) using the property that the LCP decreases by at most 1 when moving to the next suffix. The longest repeated substring is the maximum value in the LCP array.
**Complexity:** Suffix Array O(n log^2 n) with comparison sort (or O(n log n) with radix). Kasai O(n). Space O(n).

## 6. Aho-Corasick
**Correctness:** We build a trie of all patterns. Using BFS, we construct failure links pointing to the longest proper suffix that is also a prefix in the trie. During text processing, we transition through the automaton. To count occurrences efficiently without exponential copying, we use reverse-failure propagation: we count visits to each state and then push those counts back through the failure links in reverse order.
**Complexity:** Construction O(total pattern length * alphabet size). Text processing O(total text length + total number of states).

## Student Tests and Defect Detection

I created custom test cases in `tests/student/my_tests.jsonl` to verify the implementation and detect potential defects:

1. **LCS Test:** Detects issues with reconstruction. If the algorithm only returns the length but fails to construct a valid subsequence, this test will catch it.
2. **Merge Test:** Detects incorrect interval DP transitions. If the cost calculation is wrong or the merge plan doesn't use adjacent blocks properly, it will fail.
3. **Tree Test:** Detects errors in take/skip logic. If the algorithm selects adjacent vertices or fails to sum weights correctly, this test catches it.
4. **KMP Test:** Detects missing overlapping matches. If the fallback after a full match is incorrect, the overlapping start positions will be lost.
5. **Suffix Test:** Detects errors in suffix array order or Kasai LCP calculation. If suffixes are sorted incorrectly or LCP values are wrong, the `sa`, `lcp`, or `repeated` fields will mismatch.
6. **Aho-Corasick Test:** Detects issues with failure links and suffix propagation. If a pattern is a suffix of another (like "ab" in "bab"), it must be counted correctly. This test ensures overlaps and duplicates are handled.

## Sources and AI Assistance
I used AI assistance (DeepSeek) to help write and debug the Java implementations. All algorithms were verified by running the official public test suite (`python tools/run.py --check`), which passed all 44 tests. I also verified the logic of the DP transitions and string matching manually.