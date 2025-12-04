fun main() {
    data class Point(val x: Int, val y: Int) {
        operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    }

    fun Set<Point>.neighbors(it: Point): Int {
        return ((-1)..1).sumOf { i ->
            ((-1)..1).count { j ->
                (i != 0 || j != 0) && it + Point(i, j) in this
            }
        }
    }

    fun part1(input: List<String>): Int {

        val coords = input
            .flatMapIndexed { rowIndex, row ->
                row.mapIndexedNotNull { columnIndex, column ->
                    if (column == '@') Point(columnIndex, rowIndex) else null
                }
            }
            .toSet()
            .toMutableSet()
        return coords.count { coords.neighbors(it) < 4 }
    }

    fun part2(input: List<String>): Int {
        val coords = input
            .flatMapIndexed { rowIndex, row ->
                row.mapIndexedNotNull { columnIndex, column ->
                    if (column == '@') Point(columnIndex, rowIndex) else null
                }
            }
            .toSet()
            .toMutableSet()
        val initSize = coords.size
        while (true) {
            val curSize = coords.size
            coords.removeIf { coords.neighbors(it) < 4 }
            if (curSize == coords.size) return initSize - coords.size
        }
    }

    println(part1(readFile("day4_t1")))
    println(part2(readFile("day4_t1")))
    println(part1(readFile("day4")))
    println(part2(readFile("day4")))


}

