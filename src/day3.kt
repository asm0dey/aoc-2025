import kotlin.time.measureTime

fun main() {
    @Suppress("SpellCheckingInspection")
    tailrec fun maxJoltage(input: List<Int>, stepsToMake: Int = 12, currentSum: Long = 0): Long {
        if (stepsToMake == 0) return currentSum
        val indicesWithoutLastSteps = 0..input.size - stepsToMake
        val maxPossibleValue = indicesWithoutLastSteps.maxOf(input::get)
        val firstBestIndex = input.indexOfFirst(maxPossibleValue::equals)
        return maxJoltage(
            input.subList(firstBestIndex + 1, input.size),
            stepsToMake - 1,
            currentSum * 10 + input[firstBestIndex],
        )
    }

    val testInputDigitLines = readFile("day3_t1").filter(String::isNotBlank).map { it.map { it.digitToInt() } }
    val inputDigitLines = readFile("day3").filter(String::isNotBlank).map { it.map { it.digitToInt() } }
    println("Test part 1: " + testInputDigitLines.sumOf { maxJoltage(it, stepsToMake = 2) })
    println("Test part 2: " + testInputDigitLines.sumOf { maxJoltage(it) })
    println(measureTime {
        println("     part 1: " + inputDigitLines.sumOf { maxJoltage(it, stepsToMake = 2) })
        println("     part 2: " + inputDigitLines.sumOf { maxJoltage(it) })
    })
}