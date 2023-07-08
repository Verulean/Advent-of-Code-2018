package days

import adventOfCode.InputHandler
import adventOfCode.Solution

object Solution02 : Solution<List<String>>(AOC_YEAR, 2) {
    override fun getInput(handler: InputHandler): List<String> {
        return handler.getInput(delimiter = "\n")
    }

    private fun getLetterFrequencies(id: String): Set<Int> {
        val counter = HashMap<Char, Int>()
        for (char in id) {
            counter[char] = counter.getOrDefault(char, 0) + 1
        }
        return counter.values.toSet()
    }

    private fun lettersInCommon(a: String, b: String): String {
        return a.zip(b).fold("") { acc, pair ->
            if (pair.first == pair.second) acc + pair.first else acc
        }
    }

    override fun solve(input: List<String>): Pair<Int, String?> {
        // Part 1
        val letterFrequencies = input.map(Solution02::getLetterFrequencies)
        val ans1 = (letterFrequencies.count { it.contains(2) }
                * letterFrequencies.count { it.contains(3) })
        // Part 2
        solvePart2@ for (a in input) {
            for (b in input) {
                if (a.length != b.length) continue
                val shared = lettersInCommon(a, b)
                if (shared.length == a.length - 1) return Pair(ans1, shared)
            }
        }
        return Pair(ans1, null)
    }
}