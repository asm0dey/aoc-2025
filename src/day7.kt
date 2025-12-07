fun main() {
    tailrec fun List<String>.countSplitters(beams: Set<Int>, depth: Int = 0, currentSplitterCount: Int = 0): Int {
        if (depth == size) return currentSplitterCount
        val newBeams =
            beams.flatMap { beam -> if (this[depth][beam] == '^') listOf(beam - 1, beam + 1) else listOf(beam) }
        val splitCount = newBeams.size - beams.size
        return this.countSplitters(newBeams.toSet(), depth + 1, currentSplitterCount + splitCount)
    }

    tailrec fun List<String>.countBeams(beams: Map<Int, Long>, depth: Int = 0): Long {
        if (depth == size) return beams.values.sum()
        val newBeams =
            beams
                .flatMap { (beam, beamCount) ->
                    if (this[depth][beam] == '^') listOf(beam - 1 to beamCount, beam + 1 to beamCount)
                    else listOf(beam to beamCount)
                }
                .groupBy(Pair<Int, Long>::first)
                .mapValues { it.value.sumOf(Pair<Int, Long>::second) }
        return this.countBeams(newBeams, depth + 1)
    }

    fun part1(input: List<String>) = input.countSplitters(setOf(input.first().indexOf('S')))

    fun part2(input: List<String>) = input.countBeams(mapOf(input.first().indexOf('S') to 1))

    println("Test input part 1 " + part1(readFile("day7_t1")))
    println("Test input part 2 " + part2(readFile("day7_t1")))
    println("     input part 1 " + part1(readFile("day7")))
    println("     input part 2 " + part2(readFile("day7")))
}


