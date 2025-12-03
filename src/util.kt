import java.io.File

fun readFile(name: String): List<String> {
    return File("/home/finkel/work_self/aoc-2025/src/$name.txt").readLines()
}