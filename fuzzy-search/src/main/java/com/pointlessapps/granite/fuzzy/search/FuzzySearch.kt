package com.pointlessapps.granite.fuzzy.search

object FuzzySearch {
    fun <T> extract(
        query: String,
        choices: List<T>,
        maxDist: Int = 1,
    ): List<FuzzyMatch<T>> {
        if (query.isEmpty()) return emptyList()
        val q = query.lowercase()
        val m = q.length
        val minW = maxOf(1, m - maxDist)
        val maxW = m + maxDist
        val results = mutableListOf<FuzzyMatch<T>>()

        for (text in choices) {
            val tl = text.toString().length
            var best: FuzzyMatch<T>? = null

            // slide all windows of size [minW..maxW]
            for (start in 0 until tl) {
                for (w in minW..maxW) {
                    val end = start + w
                    if (end > tl) break
                    val sub = text.toString().substring(start, end).lowercase()
                    val dp = buildDP(q, sub)
                    val aligned = alignedPositions(dp, q, sub, maxDist)
                    if (aligned != null) {
                        // we have a valid match with distance = dp[m][w]
                        val dist = dp[m][sub.length]
                        // only keep if it's better than previous best
                        val isBetter = best == null ||
                                dist < best.distance ||
                                (dist == best.distance && start < best.start)
                        if (isBetter) {
                            // rebuild a highlighted snippet just for this window
                            // (we only highlight the aligned chars)
                            val absPositions = aligned.map { start + it }
                            val runs = absPositions
                                .distinct().sorted()
                                .fold(mutableListOf<IntRange>()) { acc, pos ->
                                    if (acc.isEmpty() || pos > acc.last().last + 1) {
                                        acc += pos..pos
                                    } else {
                                        val r = acc.last()
                                        acc[acc.lastIndex] = r.first..pos
                                    }
                                    acc
                                }
                            best = FuzzyMatch(text, runs, dist, start)
                        }
                    }
                }
            }
            if (best != null) results += best
        }

        return results.sorted()
    }

    fun <T> extractPath(
        query: String,
        paths: List<T>,
        maxDist: Int = 1,
    ): List<FuzzyPathMatch<T>> = paths.mapNotNull { p ->
        pathFuzzyMatch(p.toString(), query, maxDist)?.let { segs ->
            FuzzyPathMatch(p, segs)
        }
    }.sorted()
}
