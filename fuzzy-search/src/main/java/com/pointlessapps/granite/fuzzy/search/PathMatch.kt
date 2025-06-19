package com.pointlessapps.granite.fuzzy.search

data class PathMatch<T>(
    val item: T,
    val matches: List<IntRange>,
) : Comparable<PathMatch<T>> {
    override fun compareTo(other: PathMatch<T>): Int {
        val matchesSizeDiff = matches.size - other.matches.size
        if (matchesSizeDiff != 0) return matchesSizeDiff

        for (i in 0 until matches.size) {
            val matchStartDiff = matches[i].first - other.matches[i].first
            if (matchStartDiff != 0) return matchStartDiff

            val matchLengthDiff =
                (other.matches[i].last - other.matches[i].first) - (matches[i].last - matches[i].first)
            if (matchLengthDiff != 0) return matchLengthDiff
        }

        return item.toString().compareTo(item.toString())
    }
}
