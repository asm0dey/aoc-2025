fun main() {
    fun part1(input: List<String>): Long {
        val eqs = input.map { it.split(Regex("\\s+")).filter { it.isNotBlank() } }
        var sum = 0L
        for (i in eqs.last().indices) {
            val nums = eqs.map { it[i] }.dropLast(1).map { it.toLong() }
            val op = eqs.last()[i]
            when (op) {
                "+" -> sum += nums.sum()
                "*" -> sum += nums.reduce { acc, l -> acc * l }
            }
        }
        return sum
    }

    fun part2(input: List<String>): Long {
        val charArrays = input.map { it.toCharArray() }

        val maxLength = charArrays.maxOf { it.size }
        val colSeparatorIndices =
            listOf(-1) + (0..<maxLength).filter { index -> charArrays.all { index in it.indices && it[index] == ' ' } } + maxLength
        val numLines = charArrays.dropLast(1)
        var sum = 0L
        for ((start, end) in colSeparatorIndices.windowed(2, 1, false)) {
            val numbers = ((end - 1) downTo (start + 1))
                .map { colIndex ->
                    numLines.mapNotNull { it.getOrNull(colIndex) }
                        .joinToString("")
                        .trim()
                        .toLong()
                }
            when (charArrays.last()[start + 1]) {
                '+' -> sum += numbers.sum()
                '*' -> sum += numbers.reduce { acc, l -> acc * l }
            }
        }
        return sum
    }
    println(part1(readFile("day6_t1")))
    println(part2(readFile("day6_t1")))
    println(part1(readFile("day6")))
    println(part2(readFile("day6")))
}