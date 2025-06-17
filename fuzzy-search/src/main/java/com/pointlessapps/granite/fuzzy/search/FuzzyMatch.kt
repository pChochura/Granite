package com.pointlessapps.granite.fuzzy.search

data class FuzzyMatch<T>(
    val item: T,
    val matches: List<IntRange>,
    val distance: Int,
    val start: Int,
) : Comparable<FuzzyMatch<T>> {
    override fun compareTo(other: FuzzyMatch<T>) = compareValuesBy(
        a = this,
        b = other,
        { it.distance }, { it.start },
    )
}
