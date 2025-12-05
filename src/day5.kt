fun main() {
    fun part1(data: List<String>): Int {
        val lists = data.takeWhile{it.contains('-')}.map { it.split('-') }.map { it[0].toLong()..it[1].toLong() }
        val ingredients = data.takeLastWhile { it.toLongOrNull() != null }.map { it.toLong() }
        return ingredients.count { ing -> lists.any { it.contains(ing) } }
    }

    operator fun LongRange.minus(other: LongRange): List<LongRange> {
        return if (other.first in this && other.last in this) listOf(this.first..<other.first, other.last+1..this.last)
        else if (this.first in other && this.last in other) listOf()
        else if (other.first in this) listOf(this.first..<other.first)
        else if (other.last in this) listOf(other.last+1..this.last)
        else listOf(this)
    }

    operator fun List<LongRange>.minus(other: LongRange): List<LongRange> = this.flatMap { it - other }

    fun part2(data: List<String>): Long {
        val lists = data.takeWhile{it.contains('-')}.map { it.split('-') }.map { it[0].toLong()..it[1].toLong() }
        var count = 0L
        for (i in lists.indices){
            var current = listOf(lists[i])
            for (j in i+1 until lists.size){
                current = current - lists[j]
            }
            count += current.sumOf { it.last - it.first + 1 }
        }
        return count
    }
    println(part1(readFile("day5_t1")))
    println(part2(readFile("day5_t1")))
    println(part1(readFile("day5")))
    println(part2(readFile("day5")))
}

