package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.PairOf

object Solution01 : Solution<List<Int>>(AOC_YEAR, 1) {
    override fun getInput(handler: InputHandler) = handler.getInput(delimiter = "\n", transform = String::toInt)

    override fun solve(input: List<Int>): PairOf<Int> {
        // Part 1
        val ans1 = input.sum()
        // Part 2
        var frequency = 0
        var i = 0
        val seenFrequencies = HashSet<Int>()
        while (true) {
            seenFrequencies.add(frequency)
            frequency += input[i]
            if (frequency in seenFrequencies) {
                return Pair(ans1, frequency)
            }
            i = (i + 1) % input.size
        }
    }
}