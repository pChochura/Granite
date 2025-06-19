package com.pointlessapps.granite.fuzzy.search

object PathSearch {
    fun <T> extractSorted(query: String, paths: List<T>): List<PathMatch<T>> {
        val inputSegments = query.split('/')

        return paths.mapNotNull { path ->
            val segments = path.toString().split('/')
            val segmentPositions = IntArray(segments.size)
            if (segments.isNotEmpty()) {
                segmentPositions[0] = 0
                for (it in 1 until segments.size) {
                    segmentPositions[it] = segmentPositions[it - 1] + segments[it - 1].length + 1
                }
            }

            var index = -1
            val inputSegmentMatches = inputSegments.map { inputSegment ->
                // Skip previously matched segments and look for a next match
                val localIndex = segments.subList(index + 1, segments.size)
                    .indexOfFirst { it.contains(inputSegment, ignoreCase = true) }

                if (localIndex == -1) return@mapNotNull null
                index = localIndex + index + 1

                return@map index to inputSegment
            }

            PathMatch(
                item = path,
                matches = inputSegmentMatches.map { (index, querySegment) ->
                    val startIndex = segmentPositions[index]
                    val localIndex = segments[index].indexOf(querySegment, ignoreCase = true)
                    (startIndex + localIndex)..(startIndex + localIndex + querySegment.length)
                },
            )
        }.sorted()
    }
}
