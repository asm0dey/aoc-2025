import java.awt.Polygon
import java.awt.Rectangle
import kotlin.math.abs


fun main() {
    fun area(a: Pair<Long, Long>, b: Pair<Long, Long>): Long =
        (abs(a.first - b.first) + 1) * (abs(a.second - b.second) + 1)

    fun part1(input: List<String>): Long {
        val points = input.map { it.split(',').map(String::toLong).let { (a, b) -> a to b } }
        return points.indices
            .asSequence()
            .flatMap { i ->
                (i + 1..<points.size).map { j ->
                    area(points[i], points[j])
                }
            }
            .max()
    }

    fun part2(input: List<String>): Long {
        val points = input.map { it.split(',').map(String::toLong).let { (a, b) -> a to b } }

        val polygon = Polygon(
            points.map { it.first.toInt() }.toTypedArray().toIntArray(),
            points.map { it.second.toInt() }.toTypedArray().toIntArray(),
            points.size
        )

        val maxRectangle = points
            .indices
            .flatMap { i ->
                (i + 1..<points.size).map { j ->
                    val a = points[i]
                    val b = points[j]
                    area(a, b) to (a to b)
                }
            }
            .sortedByDescending { it.first }
            .first { (_, rectangles) ->
                val (pa, pb) = rectangles
                val contains = polygon.contains(
                    Rectangle(
                        minOf(pa.first, pb.first).toInt(),
                        minOf(pa.second, pb.second).toInt(),
                        abs(pa.first - pb.first).toInt(),
                        abs(pa.second - pb.second).toInt()
                    )
                )
                contains
            }
            .first

        return maxRectangle

    }
    check(part1(readFile("day9_t1")) == 50L)
    println(part1(readFile("day9")))
    check(part2(readFile("day9_t1")) == 24L)
    println(part2(readFile("day9")))
}