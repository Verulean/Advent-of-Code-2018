package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.Counter

object Solution05 : Solution<String>(AOC_YEAR, 5) {
    private var letters: Set<Char> = HashSet()

    override fun getInput(handler: InputHandler): String {
        return handler.getInput()
    }

    private fun react(polymer: String): String {
        if (polymer.length < 2) {
            return polymer
        }
        var newPolymer = polymer
        var prevLength: Int
        do {
            prevLength = newPolymer.length
            for (letter in letters) {
                val lower = letter.lowercase()
                newPolymer = newPolymer.replace(Regex("$letter$lower|$lower$letter"), "")
            }
        } while (newPolymer.length != prevLength)
        return newPolymer
    }

    private fun react(polymer: String, except: Char): String {
        return react(polymer.replace(except.toString(), "", ignoreCase = true))
    }

    override fun solve(input: String): Pair<Int, Int?> {
        letters = input.uppercase().toSet()
        val ans1 = react(input).length
        val ans2 = letters.minOfOrNull { react(input, except = it).length }
        return Pair(ans1, ans2)
    }
}