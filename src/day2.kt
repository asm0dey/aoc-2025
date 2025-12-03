import kotlin.math.pow
import kotlin.time.measureTime

fun main() {
    fun Long.countDigits(): Long {
        var x = this
        var count = 0L
        while (x > 0) {
            count++
            x /= 10
        }
        return count
    }

    fun generateNumber(pattern: Long, repetitions: Long): Long {
        var result = 0L
        var multiplier = 1L
        repeat(repetitions.toInt()) {
            result += pattern * multiplier
            multiplier *= 10.0.pow(pattern.countDigits().toDouble()).toLong()
        }
        return result
    }

    fun genAll(): Set<Long> {
        return sequence {
            for (rep in 1..9) {
                for (pattern in 1..99999) {
                    val value = generateNumber(pattern.toLong(), rep.toLong())
                    if (value <= 9999999999)
                        yield(value)
                }
            }
        }.toSet()
    }

    val all = genAll()


    fun Long.dividers(): List<Long> {
        return (1..this).filter { this % it == 0L }.reversed()
    }


    val memo = mutableMapOf<Triple<Long, Long, Long>, List<Long>>()

    fun numbersInRange(
        start: Long,
        end: Long,
        repCount: Long
    ): List<Long> = memo.getOrPut(Triple(start, end, repCount)) {
        (start..end)
            .map { pattern ->
                generateNumber(pattern, repCount)
            }
    }

    fun generateRepeated(range: LongRange, maxRepetitionsAllowed: Long = Long.MAX_VALUE): Set<Long> {
        val startDigits = range.first.countDigits()
        val finishDigits = range.last.countDigits()
        return (startDigits..finishDigits)
            .flatMap { digits ->
                digits
                    .dividers()
                    .filter { it < digits }
                    .flatMap { patternLength ->
                        val start = 10.0.pow((patternLength - 1).toDouble()).toLong()
                        val end = 10.0.pow(patternLength.toDouble()).toLong() - 1
                        val repCount = digits / patternLength
                        if (repCount <= maxRepetitionsAllowed)
                            numbersInRange(start, end, repCount)
                        else emptyList()
                    }
            }
//            .onEach { println("Trying $it") }
            .filter { it in range }
            .toSet()
    }


    fun part1(input: List<String>): Long {
        val input = input.first()
            .split(",")
            .map {
                it
                    .split("-")
                    .let { LongRange(it.first().toLong(), it.last().toLong()) }
            }
        return input.flatMap { generateRepeated(it, 2) }.sum()
    }

    fun part2(input: List<String>): Long {
        val input = input.first()
            .split(",")
            .map {
                it
                    .split("-")
                    .let { LongRange(it.first().toLong(), it.last().toLong()) }
            }
        return input.flatMap(::generateRepeated).sum()
    }


    println(part1(readFile("day2_t1")))
    println(part2(readFile("day2_t1")))
    println(measureTime {
        println(part1(readFile("day2")))
        println(part2(readFile("day2")))
    })
}
