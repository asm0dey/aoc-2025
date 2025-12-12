import kotlin.math.sign

fun main() {
    fun part1(input: List<String>): Int {
        var curPos = 50
        var counter = 0
        for (string in input) {
            if (string.startsWith("L")) {
                curPos -= string.substring(1).toInt()
                curPos = (curPos % 100 + 100) % 100
            } else if (string.startsWith("R")) {
                curPos += string.substring(1).toInt()
                curPos %= 100
            }
            if (curPos == 0) counter++
        }
        return counter
    }

    fun part2(input: List<String>): Int {
        var absolutePos = 50
        var passes = 0
        for (string in input) {
            val prevPos = absolutePos
            val value = string.substring(1).toInt()
            val dir = if (string.startsWith("L")) -1 else 1
            absolutePos += value * dir
            passes +=
                if (dir > 0) absolutePos.floorDiv(100)
                else prevPos.sign - 1 - (absolutePos - 1).floorDiv(100)
            absolutePos = (absolutePos % 100 + 100) % 100
        }
        return passes
    }

    val testInput1 = readFile("day1_t1")
    println(part1(testInput1))
    println(part2(testInput1))
    val input = readFile("day1")
    println(part1(input))
    println(part2(input))
}