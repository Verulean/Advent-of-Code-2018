package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.ints

object Solution09 : Solution<Pair<Int, Int>>(AOC_YEAR, 9) {
    override fun getInput(handler: InputHandler): Pair<Int, Int> {
        return handler.getInput().ints { Pair(it[0], it[1]) }
    }

    private fun winningScore(players: Int, maxMarble: Int): UInt {
        val scores = MutableList(players) { 0u }
        val clockwise = MutableList(maxMarble + 1) { 0 }
        val counterclockwise = MutableList(maxMarble + 1) { 0 }
        var currentMarble = 0
        for (marbleValue in 1..maxMarble) {
            if (marbleValue % 23 != 0) {
                val next = clockwise[currentMarble]
                val nextNext = clockwise[next]
                clockwise[next] = marbleValue
                clockwise[marbleValue] = nextNext
                counterclockwise[nextNext] = marbleValue
                counterclockwise[marbleValue] = next
                currentMarble = marbleValue
            } else {
                val removedMarble = (0 until 7).fold(currentMarble) { acc, _ -> counterclockwise[acc] }
                val prev = counterclockwise[removedMarble]
                val next = clockwise[removedMarble]
                clockwise[prev] = next
                clockwise[removedMarble] = 0
                counterclockwise[next] = prev
                counterclockwise[removedMarble] = 0
                currentMarble = next
                scores[marbleValue % players] += (marbleValue + removedMarble).toUInt()
            }
        }
        return scores.max()
    }

    override fun solve(input: Pair<Int, Int>): Pair<UInt, UInt> {
        val (players, maxScore) = input
        return Pair(
            winningScore(players, maxScore),
            winningScore(players, maxScore * 100)
        )
    }
}