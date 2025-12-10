import org.chocosolver.solver.Model
import org.chocosolver.solver.Solution
import kotlin.math.pow
import kotlin.time.measureTime


fun main() {
    fun part1(input: List<String>): Int {
        val parsedInput = input
            .map { it.split(" ").dropLast(1) }
            .map {
                val binString = it.first().drop(1).dropLast(1).map { if (it == '#') 1 else 0 }.joinToString("")
                val bin = binString.toInt(2)
                val buttons = it.drop(1).map {
                    val switchesLamps = it.drop(1).dropLast(1).split(',').map { it.toInt() }
                    val binSwitches = IntArray(binString.length) { 0 }
                    for (i in switchesLamps) {
                        binSwitches[i] = 1
                    }
                    binSwitches.joinToString("").toInt(2)
                }
                bin to buttons
            }
        val totalPresses = parsedInput.sumOf { (targetState, buttonsList) ->
            val numButtons = buttonsList.size
            val maxCombinations = 2.0.pow(numButtons).toInt()

            var minPresses = Int.MAX_VALUE

            for (i in 0 until maxCombinations) {
                var currentState = 0
                var pressCount = 0
                val binaryString = i.toString(2).padStart(numButtons, '0')

                for (btnIdx in 0 until numButtons) {
                    if (binaryString[numButtons - 1 - btnIdx] == '1') {
                        currentState = currentState xor buttonsList[btnIdx]
                        pressCount++
                    }
                }
                if (currentState == targetState) {
                    if (pressCount < minPresses) {
                        minPresses = pressCount
                    }
                }
            }

            if (minPresses == Int.MAX_VALUE) 0 else minPresses
        }

        println("Fewest total presses: $totalPresses")
        return 0
    }

    fun part2(input: List<String>): Int {
        val parsedInput = input
            .map { it.split(" ").drop(1) }
            .map {
//                val binString = it.first().drop(1).dropLast(1).map { if (it == '#') 1 else 0 }.joinToString("")
//                val bin = binString.toInt(2)
                val buttons = it.dropLast(1).map {
                    it.drop(1).dropLast(1).split(',').map { it.toInt() }
                }
                val target = it.last().drop(1).dropLast(1).split(',').map { it.toInt() }
                buttons to target
            }
        // a{n} +b{m,k} +c{k} = {n,m,k}
        // (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}

        return parsedInput.sumOf { (buttons, targetCounterState) ->
            val model = Model()
            val ks = model.intVarArray(buttons.size, 0, targetCounterState.max())
            for (counterIndex in targetCounterState.indices) {
                buttons
                    .mapIndexed { index, counters -> if (counters.contains(counterIndex)) index else -1 }
                    .filter { it != -1 }
                    .map { ks[it] }
                    .toTypedArray()
                    .let {
                        model.sum(it, "=", targetCounterState[counterIndex]).post()
                    }
            }
            val maxSum = model.intVar("maxSum", targetCounterState.max(), targetCounterState.max() * buttons.size)
            model.sum(ks, "=", maxSum).post()
            var bestSolLength = Int.MAX_VALUE
            while (model.solver.solve()) {
                val intVal = Solution(model).record().getIntVal(maxSum)
                if (intVal < bestSolLength) {
                    bestSolLength = intVal
                }
            }
            bestSolLength
        }

    }

    val testInput = """[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}""".lines()
    part1(testInput)
    part1(readFile("day10"))
    println(part2(testInput))
    println(measureTime {
        println(part2(readFile("day10")))
    })
}
// 0101