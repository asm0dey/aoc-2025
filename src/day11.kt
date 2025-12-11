import org.jgrapht.alg.shortestpath.AllDirectedPaths
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import org.jgrapht.traverse.TopologicalOrderIterator


fun main() {
    fun part1(input: List<String>): Int {
        val lines = input.map { it.split(" ", ":") }
        val graph = DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
        lines.flatten().toSet().forEach {
            graph.addVertex(it)
        }
        lines.forEach { line ->
            line.drop(1).forEach { graph.addEdge(line.first(), it) }
        }
        return AllDirectedPaths(graph).getAllPaths("you", "out", true, null).size
    }

    fun SimpleDirectedGraph<String, DefaultEdge>.countPathsFromStart(
        sortedVertices: List<String>,
        start: String,
        target: String
    ): Long {
        val pathsCount = hashMapOf(start to 1L)
        for (vertex in sortedVertices) {
            val count = pathsCount.getOrDefault(vertex, 0L)
            if (count == 0L) continue
            for (edge in outgoingEdgesOf(vertex)) {
                val target = getEdgeTarget(edge)
                pathsCount[target] = pathsCount.getOrDefault(target, 0L) + count
            }
        }
        return pathsCount[target] ?: 0L
    }

    fun part2(input: List<String>): Any {
        val lines = input.map { it.split(" ", ":") }
        val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
        lines.asSequence().flatten().filterNot { it.isBlank() }.toSet().forEach {
            graph.addVertex(it)
        }
        lines.forEach { line ->
            val drop = line.drop(2)
            drop.forEach { graph.addEdge(line.first(), it) }
        }
        val sortedVertices = TopologicalOrderIterator(graph).asSequence().toList()
        val docToFftExists = DijkstraShortestPath(graph).getPath("dac", "fft") != null
        val start = "svr"
        val second: String
        val third: String
        val target = "out"
        if (docToFftExists) {
            second = "dac"
            third = "fft"
        } else {
            second = "fft"
            third = "dac"
        }
        return listOf(start, second, third,target)
            .windowed(2, 1, false)
            .map { graph.countPathsFromStart(sortedVertices, it[0], it[1]) }
            .reduce { acc, l -> acc * l }
    }
    println(part1(readFile("day11_t1")))
    println(part1(readFile("day11")))
    println(part2(readFile("day11_t2")))
    println(part2(readFile("day11")))
}