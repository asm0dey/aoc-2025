import kotlin.time.measureTime

fun main() {
    data class Point(val x: Int, val y: Int) {
        operator fun minus(other: Point) = Point(x - other.x, y - other.y)
        operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    }

    fun normalize(points: List<Point>): List<Point> {
        val minX = points.minOf { it.x }
        val minY = points.minOf { it.y }
        val offset = Point(minX, minY)
        return points.map { it - offset }.sortedWith(compareBy({ it.y }, { it.x }))
    }

    data class Figure(val index: Int, val area: Int, val cells: List<Point>) {
        val w = cells.maxOf { it.x } + 1
        val h = cells.maxOf { it.y } + 1
        fun rotate(p: Point) = Point(-p.y, p.x)
        fun flipX(p: Point) = Point(-p.x, p.y)
        val permutations by lazy {
            val variants = hashSetOf<List<Point>>()
            var current = cells
            repeat(4) {
                val nrm = normalize(current)
                variants.add(nrm)
                val flipped = normalize(current.map(::flipX))
                variants.add(flipped)
                current = current.map(::rotate)
            }
            variants.map { Figure(index, area, it) }
        }
    }

    data class ShapeCount(val shapeIndex: Int, val count: Int)

    data class Region(val width: Int, val height: Int, val counts: List<ShapeCount>)

    fun parse(input: List<String>): Pair<List<Figure>, List<Region>> {
        val shapesRaw = mutableMapOf<Int, List<String>>()
        var i = 0
        while (i < input.size) {
            val line = input[i].trim()
            if (line.isBlank()) {
                i++; continue
            }
            val idxMatch = Regex("^(\\d+):\\s*").matchEntire(line)
            if (idxMatch != null) {
                val idx = idxMatch.groupValues[1].toInt()
                i++
                val gridLines = mutableListOf<String>()
                while (i < input.size && input[i].isNotBlank()) {
                    gridLines.add(input[i])
                    i++
                }
                shapesRaw[idx] = gridLines
                continue
            }
            // Not a shape header -> regions start
            break
        }
        // Move index to first region line
        while (i < input.size && input[i].isBlank()) i++
        val regions = mutableListOf<Region>()

        while (i < input.size) {
            val line = input[i].trim()
            if (line.isBlank()) {
                i++; continue
            }
            val m = Regex("^(\\d+)x(\\d+):\\s+(.+)").matchEntire(line)
            if (m != null) {
                val w = m.groupValues[1].toInt()
                val h = m.groupValues[2].toInt()
                val counts = m.groupValues[3].trim().split(Regex("\\s+")).map { it.toInt() }
                val shapeCountsList = counts.mapIndexedNotNull { idx, count ->
                    if (count > 0) ShapeCount(idx, count) else null
                }
                regions.add(Region(w, h, shapeCountsList))
            }
            i++
        }

        val figures = shapesRaw.toSortedMap().map { (idx, gridLines) ->
            val pts = gridLines
                .flatMapIndexed { y, row ->
                    row.mapIndexed { x, ch ->
                        if (ch == '#') Point(x, y) else null
                    }
                }
                .filterNotNull()
            Figure(idx, pts.size, normalize(pts))
        }
        return figures to regions
    }

    fun canFit(figures: List<Figure>, region: Region): Boolean {
        val (w, h, counts) = region
        val shapeCounts = counts.associate { it.shapeIndex to it.count }.toMutableMap()
        val totalArea = counts.sumOf { figures[it.shapeIndex].area * it.count }
        // Presents do NOT have to fill the entire region; they just must fit without overlap.
        // Quick infeasibility check: required area can't exceed region area.
        if (totalArea > w * h) return false

        val board = mutableSetOf<Point>()
        var remainingArea = totalArea

        // Precompute shapes with counts > 0 to speed up
        val indices = counts.map { it.shapeIndex }
        if (indices.isEmpty()) return true

        // Order shape indices by area descending to reduce branching
        val order = indices.sortedByDescending { figures[it].area }

        fun firstEmpty(): Point? {
            for (y in 0 until h)
                for (x in 0 until w) {
                    val point = Point(x, y)
                    if (point !in board) return point
                }
            return null
        }

        fun place(delta: Point, figure: Figure, set: Boolean) {
            for (p in figure.cells) {
                val pt = p + delta
                if (set) board.add(pt) else board.remove(pt)
            }
        }

        fun fits(delta: Point, figure: Figure): Boolean {
            for (p in figure.cells) {
                val (x, y) = p + delta
                if (x !in 0 until w || y !in 0 until h) return false
                if (Point(x, y) in board) return false
            }
            return true
        }

        fun dfs(): Boolean {
            if (order.all { shapeIdx -> shapeCounts.getOrDefault(shapeIdx, 0) == 0 }) return true
            val remainingEmpty = w * h - board.size
            if (remainingArea > remainingEmpty) return false
            val targetPoint = firstEmpty() ?: return false
            // Try shape types with remaining count
            for (shapeIdx in order) {
                if (shapeCounts.getOrDefault(shapeIdx, 0) <= 0) continue
                val shape = figures[shapeIdx]
                for (perm in shape.permutations) {
                    for (p in perm.cells) {
                        val delta = targetPoint - p
                        val (dx, dy) = delta
                        if (dx < 0 || dy < 0 || dx + perm.w > w || dy + perm.h > h) continue
                        if (fits(delta, perm)) {
                            place(delta, perm, true)
                            shapeCounts[shapeIdx] = shapeCounts[shapeIdx]!! - 1
                            val a = perm.cells.size
                            remainingArea -= a
                            if (dfs()) return true
                            shapeCounts[shapeIdx] = shapeCounts[shapeIdx]!! + 1
                            remainingArea += a
                            place(delta, perm, false)
                        }
                    }
                }
            }
            // Option to leave this cell empty (unused space)
            board.add(targetPoint)
            if (dfs()) return true
            board.remove(targetPoint)
            return false
        }

        return dfs()
    }

    fun part1(input: List<String>): Int {
        val (shapes, regions) = parse(input)
        return regions.count { canFit(shapes, it) }
    }

    // Run on test and actual input
    println(part1(readFile("day12_t1")))
    println(measureTime {
        println(part1(readFile("day12")))
    })
}