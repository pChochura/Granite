package com.pointlessapps.granite.fuzzy.search

/**
 * Build the full DP matrix for Levenshtein distance.
 * dp[i][j] = distance between q[0..i) and w[0..j).
 */
internal fun buildDP(q: String, w: String): Array<IntArray> {
    val m = q.length
    val n = w.length
    val dp = Array(m + 1) { IntArray(n + 1) }
    for (i in 0..m) dp[i][0] = i
    for (j in 0..n) dp[0][j] = j
    for (i in 1..m) {
        for (j in 1..n) {
            val cost = if (q[i - 1] == w[j - 1]) 0 else 1
            dp[i][j] = minOf(
                dp[i - 1][j] + 1,
                dp[i][j - 1] + 1,
                dp[i - 1][j - 1] + cost
            )
        }
    }
    return dp
}

/**
 * Given dp[][], back–trace from dp[m][n] to dp[0][0] and
 * return the list of positions j-1 in the *window* w that
 * align to some character in q.
 *
 * If dp[m][n] > maxDist return null.
 */
internal fun alignedPositions(
    dp: Array<IntArray>,
    q: String,
    w: String,
    maxDist: Int,
): List<Int>? {
    val m = q.length
    val n = w.length
    if (dp[m][n] > maxDist) return null

    var i = m
    var j = n
    val aligned = mutableListOf<Int>()
    while (i > 0 || j > 0) {
        // substitution or match
        if (i > 0 && j > 0 &&
            dp[i][j] == dp[i - 1][j - 1] + if (q[i - 1] == w[j - 1]) 0 else 1
        ) {
            // q[i-1] aligned to w[j-1]
            aligned += j - 1
            i--; j--
        }
        // deletion from q (skip q[i-1])
        else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
            i--
        }
        // insertion into q (skip w[j-1])
        else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
            j--
        } else {
            // should not happen
            break
        }
    }
    return aligned.reversed()
}
