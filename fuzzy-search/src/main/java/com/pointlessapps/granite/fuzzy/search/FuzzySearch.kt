package com.pointlessapps.granite.fuzzy.search

object FuzzySearch {
    fun <T> extractPathsSorted(query: String, paths: List<T>): List<SearchMatch<T>> =
        paths.mapNotNull { path ->
            SegmentedSearch.extractRanges(query, path.toString(), segmentDelimiter = '/')
                ?.let { SearchMatch(path, it) }
        }.sorted()

    fun extractWords(query: String, text: String): SearchMatch<String>? =
        SegmentedSearch.extractRanges(query, text, segmentDelimiter = ' ')
            ?.let { SearchMatch(text, it) }
}
