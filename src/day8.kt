import kotlin.math.sqrt
import kotlin.time.measureTime

fun main() {
    data class Point3(val x: Long, val y: Long, val z: Long) {
        fun distanceTo(other: Point3): Double {
            return sqrt((x - other.x).toDouble() * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z))
        }

        override fun toString(): String {
            return "[$x, $y, $z]"
        }
    }

    fun part1(input: List<String>, connectors: Int): Long {
        val boxes = input.map { it.split(",").map(String::toLong) }.map { Point3(it[0], it[1], it[2]) }
        val closestPairs = sequence {
            for (i in boxes.indices) for (j in i + 1 until boxes.size) yield(boxes[i] to boxes[j])
        }.sortedBy { (a, b) -> a.distanceTo(b) }.take(connectors)
        val chains = mutableListOf<MutableSet<Point3>>()
        for ((a, b) in closestPairs) {
            val aChain = chains.firstOrNull { it.contains(a) }
            val bChain = chains.firstOrNull { it.contains(b) }
            if (aChain != null && aChain == bChain) continue
            if (aChain != null && bChain != null) {
                chains.remove(bChain)
                aChain.addAll(bChain)
            } else if (aChain == null && bChain == null) {
                chains.add(hashSetOf(a, b))
            } else if (aChain != null) {
                aChain.add(a)
                aChain.add(b)
            } else if (bChain != null) {
                bChain.add(a)
                bChain.add(b)
            }
            if (chains.size == 1 && chains[0].size == boxes.size) return a.x * b.x
        }
        return chains.map { it.size }.sortedDescending().take(3).reduce(Int::times).toLong()
    }

    check(part1(readFile("day8_t1"), 10) == 40L)
    check(part1(readFile("day8_t1"), Int.MAX_VALUE) == 25272L)
    println(measureTime {
        println(part1(readFile("day8"), 1000))
        println(part1(readFile("day8"), Int.MAX_VALUE))
    })
}