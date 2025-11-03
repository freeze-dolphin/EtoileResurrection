package io.sn.etoile.launch

fun greedyPartition(works: Int, jobs: Int): List<Int> {
    require(works >= 0 && jobs > 0) { "require: n >= 0 && x > 0" }

    if (jobs == 1) return listOf(works)

    val base = works / jobs
    val remainder = works % jobs

    return mutableListOf<Int>().apply {
        repeat(remainder) { add(base + 1) }
        repeat(jobs - remainder) { add(base) }
    }
}