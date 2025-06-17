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

internal fun mergePositions(positions: List<Int>): List<IntRange> {
    if (positions.isEmpty()) return emptyList()
    val sorted = positions.distinct().sorted()
    val runs = mutableListOf<IntRange>()
    var start = sorted[0]
    var end = start
    for (p in sorted.drop(1)) {
        if (p == end + 1) {
            end = p
        } else {
            runs += start..end
            start = p; end = p
        }
    }
    runs += start..end

    return runs
}

internal fun pathFuzzyMatch(
    path: String,
    query: String,
    maxDist: Int = 1,
): List<SegMatch>? {
    val qSegs = query.split("/")
    val tSegs = path.split("/")
    val qn = qSegs.size
    val tn = tSegs.size

    // precompute segment-start offsets in the full path
    val segStarts = IntArray(tn)
    if (tn > 0) {
        segStarts[0] = 0
        for (it in 1 until tn) {
            segStarts[it] = segStarts[it - 1] + tSegs[it - 1].length + 1
        }
    }

    data class Cell(val dist: Int, val prevK: Int, val match: SegMatch?)
    // dp[i][j]: best total dist matching qSegs[0..i) into tSegs[0..j-1]
    val dp = Array(qn + 1) { Array<Cell?>(tn + 1) { null } }
    dp[0][0] = Cell(0, -1, null)

    for (i in 0 until qn) {
        for (j in 0..tn) {
            val cell = dp[i][j] ?: continue
            // try matching qSegs[i] → each tSegs[k-1], for k in (j+1)..tn
            for (k in j + 1..tn) {
                val realK = k - 1
                val sub = tSegs[realK]
                val dpMat = buildDP(qSegs[i], sub)
                val d = dpMat[qSegs[i].length][sub.length]
                if (d <= maxDist) {
                    val aligned = alignedPositions(dpMat, qSegs[i], sub, maxDist)!!
                        .map { segStarts[realK] + it }
                    val m = SegMatch(d, aligned)
                    val nd = cell.dist + d
                    val old = dp[i + 1][k]
                    if (old == null || nd < old.dist) {
                        dp[i + 1][k] = Cell(nd, j, m)
                    }
                }
            }
        }
    }

    // pick the best end‐cell in row i = qn, over columns k=1..tn
    val bestEnd = dp[qn]
        .mapIndexedNotNull { k, c -> c?.let { k to it.dist } }
        .minByOrNull { it.second }
        ?: return null

    // back‐trace to collect the SegMatch in reverse
    val segMatches = mutableListOf<SegMatch>()
    var ci = qn
    var cj = bestEnd.first
    while (ci > 0) {
        val cell = dp[ci][cj]!!
        cell.match?.let { segMatches += it }
        cj = cell.prevK
        ci--
    }

    return segMatches.reversed()
}
