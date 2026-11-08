import java.util.*;
/** Implement these algorithms. docs/guide/CONTRACTS.md defines each field and limit. */
public final class Algorithms {
    /** Compare two versions of a sequence while preserving the order of shared characters. Implement lcs according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> lcs(Map<String,Object> input) {
        String a = (String) input.get("a");
        String b = (String) input.get("b");

        int m = a.length();
        int n = b.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                sb.append(a.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        String sequence = sb.reverse().toString();
        int length = dp[m][n];

        Map<String, Object> result = new HashMap<>();
        result.put("length", length);
        result.put("sequence", sequence);
        return result;
    }
    /** Plan the cheapest sequence of adjacent block merges when the original order must be preserved. Implement merge according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> merge(Map<String,Object> input) {
        List<Object> sizesRaw = (List<Object>) input.get("sizes");
        int n = sizesRaw.size();

        List<Object> plan = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put("plan", plan);

        if (n <= 1) {
            result.put("cost", 0L);
            return result;
        }

        long[] sizes = new long[n];
        for (int i = 0; i < n; i++) {
            sizes[i] = (Long) sizesRaw.get(i);
        }

        long[] pref = new long[n + 1];
        for (int i = 0; i < n; i++) {
            pref[i + 1] = pref[i] + sizes[i];
        }

        long[][] dp = new long[n][n];
        int[][] split = new int[n][n];

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                dp[i][j] = Long.MAX_VALUE;

                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j] + (pref[j + 1] - pref[i]);
                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                        split[i][j] = k;
                    }
                }
            }
        }

        buildPlan(0, n - 1, split, plan);

        result.put("cost", dp[0][n - 1]);
        return result;
    }

    private static void buildPlan(int i, int j, int[][] split, List<Object> plan) {
        if (i == j) return;

        int k = split[i][j];
        buildPlan(i, k, split, plan);
        buildPlan(k + 1, j, split, plan);

        List<Object> step = new ArrayList<>();
        step.add(i);
        step.add(k);
        step.add(j);
        plan.add(step);
    }
    /** Choose a maximum-value set when neighbouring vertices are incompatible and conflicts form a tree. Implement tree according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> tree(Map<String,Object> input) {
        long nLong = (Long) input.get("n");
        int n = (int) nLong;

        Map<String, Object> result = new HashMap<>();

        if (n == 0) {
            result.put("weight", 0L);
            result.put("selected", new ArrayList<>());
            return result;
        }

        List<Object> weightsRaw = (List<Object>) input.get("weights");
        List<Object> edgesRaw = (List<Object>) input.get("edges");

        long[] weights = new long[n];
        for (int i = 0; i < n; i++) {
            weights[i] = (Long) weightsRaw.get(i);
        }

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

        for (Object edgeObj : edgesRaw) {
            List<Object> edge = (List<Object>) edgeObj;
            int u = ((Long) edge.get(0)).intValue();
            int v = ((Long) edge.get(1)).intValue();
            adj.get(u).add(v);
            adj.get(v).add(u);
        }

        long[] in = new long[n];
        long[] out = new long[n];

        int[] parent = new int[n];
        Arrays.fill(parent, -2);

        List<Integer> order = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();

        stack.push(0);
        parent[0] = -1;

        while (!stack.isEmpty()) {
            int u = stack.pop();
            order.add(u);

            for (int v : adj.get(u)) {
                if (parent[v] == -2) {
                    parent[v] = u;
                    stack.push(v);
                }
            }
        }

        for (int i = order.size() - 1; i >= 0; i--) {
            int u = order.get(i);

            in[u] = weights[u];
            out[u] = 0;

            for (int v : adj.get(u)) {
                if (parent[v] == u) {
                    in[u] += out[v];
                    out[u] += Math.max(in[v], out[v]);
                }
            }
        }

        List<Object> selected = new ArrayList<>();
        Stack<int[]> recStack = new Stack<>();
        recStack.push(new int[]{0, (in[0] > out[0]) ? 1 : 0});

        while (!recStack.isEmpty()) {
            int[] pair = recStack.pop();
            int u = pair[0];
            boolean take = (pair[1] == 1);

            if (take) {
                selected.add((long)u);
                for (int v : adj.get(u)) {
                    if (parent[v] == u) {
                        recStack.push(new int[]{v, 0});
                    }
                }
            } else {
                for (int v : adj.get(u)) {
                    if (parent[v] == u) {
                        boolean takeChild = (in[v] > out[v]);
                        recStack.push(new int[]{v, takeChild ? 1 : 0});
                    }
                }
            }
        }

        result.put("weight", Math.max(in[0], out[0]));
        result.put("selected", selected);
        return result;
    }
    /** Establish a simple exact-search baseline against which the other two searches can be checked. Implement naive according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> naive(Map<String,Object> input) {
        String text = (String) input.get("text");
        String pattern = (String) input.get("pattern");

        int n = text.length();
        int m = pattern.length();

        List<Object> positions = new ArrayList<>();

        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                positions.add((long) i);
            }
        } else {
            for (int i = 0; i <= n - m; i++) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    positions.add((long) i);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("positions", positions);
        return result;
    }
    /** Reuse information about matched prefixes to avoid restarting the search after every mismatch. Implement kmp according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> kmp(Map<String,Object> input) {
        String text = (String) input.get("text");
        String pattern = (String) input.get("pattern");

        int n = text.length();
        int m = pattern.length();

        List<Object> positions = new ArrayList<>();

        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                positions.add((long) i);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("positions", positions);
            return result;
        }

        int[] pi = new int[m];
        for (int i = 1, k = 0; i < m; i++) {
            while (k > 0 && pattern.charAt(i) != pattern.charAt(k)) {
                k = pi[k - 1];
            }
            if (pattern.charAt(i) == pattern.charAt(k)) {
                k++;
            }
            pi[i] = k;
        }


        for (int i = 0, k = 0; i < n; i++) {
            while (k > 0 && text.charAt(i) != pattern.charAt(k)) {
                k = pi[k - 1];
            }
            if (text.charAt(i) == pattern.charAt(k)) {
                k++;
            }
            if (k == m) {
                positions.add((long) (i - m + 1));
                k = pi[k - 1];
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("positions", positions);
        return result;
    }
    /** Reject windows with unequal fingerprints and verify every equal-hash candidate. Implement rk according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> rk(Map<String,Object> input) {
        String text = (String) input.get("text");
        String pattern = (String) input.get("pattern");

        int n = text.length();
        int m = pattern.length();

        List<Object> positions = new ArrayList<>();

        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                positions.add((long) i);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("positions", positions);
            return result;
        }

        if (m > n) {
            Map<String, Object> result = new HashMap<>();
            result.put("positions", positions);
            return result;
        }

        long base = 31;
        long mod = 101;
        long pHash = 0;
        long tHash = 0;
        long h = 1;

        for (int i = 0; i < m - 1; i++) {
            h = (h * base) % mod;
        }

        for (int i = 0; i < m; i++) {
            pHash = (pHash * base + pattern.charAt(i)) % mod;
            tHash = (tHash * base + text.charAt(i)) % mod;
        }

        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    positions.add((long) i);
                }
            }

            if (i < n - m) {
                tHash = (tHash - text.charAt(i) * h) % mod;
                if (tHash < 0) tHash += mod;
                tHash = (tHash * base + text.charAt(i + m)) % mod;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("positions", positions);
        return result;
    }
    /** Order the text suffixes and use adjacent common prefixes to find repeated substrings. Implement suffix according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> suffix(Map<String,Object> input) {
        String text = (String) input.get("text");
        int n = text.length();

        List<Object> saList = new ArrayList<>();
        List<Object> lcpList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();

        if (n == 0) {
            result.put("sa", saList);
            result.put("lcp", lcpList);
            result.put("repeated", "");
            return result;
        }

        Integer[] sa = new Integer[n];
        for (int i = 0; i < n; i++) sa[i] = i;

        int[] rank = new int[n];
        int[] tmp = new int[n];

        for (int i = 0; i < n; i++) rank[i] = text.charAt(i);

        for (int k = 1; k < n; k *= 2) {
            final int kk = k;
            Arrays.sort(sa, (a, b) -> {
                if (rank[a] != rank[b]) return Integer.compare(rank[a], rank[b]);
                int ra = (a + kk < n) ? rank[a + kk] : -1;
                int rb = (b + kk < n) ? rank[b + kk] : -1;
                return Integer.compare(ra, rb);
            });

            tmp[sa[0]] = 0;
            for (int i = 1; i < n; i++) {
                int curr = sa[i];
                int prev = sa[i - 1];
                int currSecond = (curr + kk < n) ? rank[curr + kk] : -1;
                int prevSecond = (prev + kk < n) ? rank[prev + kk] : -1;
                if (rank[curr] == rank[prev] && currSecond == prevSecond) {
                    tmp[curr] = tmp[prev];
                } else {
                    tmp[curr] = tmp[prev] + 1;
                }
            }
            System.arraycopy(tmp, 0, rank, 0, n);
        }

        for (int i = 0; i < n; i++) saList.add((long) sa[i]);

        int[] pos = new int[n];
        for (int i = 0; i < n; i++) pos[sa[i]] = i;

        int[] lcp = new int[n];
        int h = 0;
        for (int i = 0; i < n; i++) {
            if (pos[i] > 0) {
                int j = sa[pos[i] - 1];
                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                    h++;
                }
                lcp[pos[i]] = h;
                if (h > 0) h--;
            }
        }

        int maxLcp = 0;
        int maxIdx = 0;
        for (int i = 0; i < n; i++) {
            lcpList.add((long) lcp[i]);
            if (lcp[i] > maxLcp) {
                maxLcp = lcp[i];
                maxIdx = i;
            }
        }

        String repeated = "";
        if (maxLcp > 0) {
            int start = sa[maxIdx];
            repeated = text.substring(start, start + maxLcp);
        }

        result.put("sa", saList);
        result.put("lcp", lcpList);
        result.put("repeated", repeated);
        return result;
    }
    /** Build one dictionary automaton and count its patterns in several independent texts. Implement aho according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> aho(Map<String,Object> input) {
        List<Object> patternsRaw = (List<Object>) input.get("patterns");
        List<Object> textsRaw = (List<Object>) input.get("texts");

        int p = patternsRaw.size();
        List<String> patterns = new ArrayList<>();
        for (Object o : patternsRaw) patterns.add((String) o);

        List<int[]> next = new ArrayList<>();
        next.add(new int[128]);
        Arrays.fill(next.get(0), -1);

        List<Integer> fail = new ArrayList<>();
        fail.add(0);

        List<Integer> outCount = new ArrayList<>();
        outCount.add(0);

        for (int i = 0; i < p; i++) {
            String pat = patterns.get(i);
            int v = 0;
            for (int j = 0; j < pat.length(); j++) {
                char c = pat.charAt(j);
                if (next.get(v)[c] == -1) {
                    next.get(v)[c] = next.size();
                    next.add(new int[128]);
                    Arrays.fill(next.get(next.size() - 1), -1);
                    fail.add(0);
                    outCount.add(0);
                }
                v = next.get(v)[c];
            }
            outCount.set(v, outCount.get(v) + 1);
        }

        int S = next.size();

        java.util.Queue<Integer> q = new java.util.LinkedList<>();
        for (int c = 0; c < 128; c++) {
            if (next.get(0)[c] != -1) {
                int v = next.get(0)[c];
                fail.set(v, 0);
                q.add(v);
            } else {
                next.get(0)[c] = 0;
            }
        }

        while (!q.isEmpty()) {
            int v = q.poll();
            int f = fail.get(v);
            outCount.set(v, outCount.get(v) + outCount.get(f));

            for (int c = 0; c < 128; c++) {
                if (next.get(v)[c] != -1) {
                    int u = next.get(v)[c];
                    fail.set(u, next.get(f)[c]);
                    q.add(u);
                } else {
                    next.get(v)[c] = next.get(f)[c];
                }
            }
        }

        List<Object> counts = new ArrayList<>();

        for (Object textObj : textsRaw) {
            String text = (String) textObj;

            long[] visits = new long[S];

            int v = 0;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                v = next.get(v)[c];
                visits[v]++;
            }

            for (int i = S - 1; i > 0; i--) {
                visits[fail.get(i)] += visits[i];
            }

            List<Object> row = new ArrayList<>();
            for (int i = 0; i < p; i++) {
                String pat = patterns.get(i);
                int u = 0;
                for (int j = 0; j < pat.length(); j++) {
                    u = next.get(u)[pat.charAt(j)];
                }
                row.add(visits[u]);
            }
            counts.add(row);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("counts", counts);
        return result;
    }
}
