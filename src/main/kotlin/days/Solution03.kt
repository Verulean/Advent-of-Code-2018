package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.Counter
import adventOfCode.util.ints

object Solution03 : Solution<List<List<Int>>>(AOC_YEAR, 3) {
    override fun getInput(handler: InputHandler): List<List<Int>> {
        return handler.getInput(delimiter = "\n", transform = String::ints)
    }

    override fun solve(input: List<List<Int>>): Pair<Int, Int?> {
        // Part 1
        val tiles = Counter<Pair<Int, Int>>()
        val candidates = HashSet<Int>()
        for ((claimKey, i, j, di, dj) in input) {
            var hasOverlap = false
            for (ii in i until i + di) {
                for (jj in j until j + dj) {
                    val pos = Pair(ii, jj)
                    val count = tiles[pos]
                    if (count > 0) {
                        hasOverlap = true
                    }
                    tiles[pos] = count + 1
                }
            }
            if (!hasOverlap) {
                candidates.add(claimKey)
            }
        }
        val ans1 = tiles.values.count { it > 1 }
        // Part 2
        for ((claimKey, i, j, di, dj) in input) {
            if (!candidates.contains(claimKey)) continue
            if ((i until i + di).all { ii ->
                    (j until j + dj).all { jj ->
                        tiles[Pair(ii, jj)]!! <= 1
                    }
            }) return Pair(ans1, claimKey)
        }
        return Pair(ans1, null)
    }
}