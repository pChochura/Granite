package com.pointlessapps.granite.fuzzy.search

class FuzzyPathMatch<T> : Comparable<FuzzyPathMatch<T>> {
    val item: T
    val totalDist: Int
    val allPositions: List<Int>
    val firstPos: Int
    val matches: List<IntRange>

    constructor(item: T) {
        this.item = item
        totalDist = 0
        allPositions = emptyList()
        firstPos = 0
        matches = emptyList()
    }

    internal constructor(
        item: T,
        segMatches: List<SegMatch>,
    ) {
        this.item = item
        totalDist = segMatches.sumOf { it.dist }
        allPositions = segMatches.flatMap { it.absPositions }
        firstPos = allPositions.minOrNull() ?: Int.MAX_VALUE
        matches = mergePositions(allPositions)
    }

    override fun compareTo(other: FuzzyPathMatch<T>) = compareValuesBy(
        a = this,
        b = other,
        { it.totalDist }, { it.firstPos },
    )
}

internal data class SegMatch(
    val dist: Int,
    val absPositions: List<Int>,
)
