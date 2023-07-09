package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.PairOf
import adventOfCode.util.ints

object Solution09 : Solution<PairOf<Int>>(AOC_YEAR, 9) {
    override fun getInput(handler: InputHandler): PairOf<Int> {
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
                repeat(6) { currentMarble = counterclockwise[currentMarble] }
                val next = counterclockwise[currentMarble]
                val nextNext = counterclockwise[next]
                clockwise[nextNext] = currentMarble
                counterclockwise[currentMarble] = nextNext
                scores[marbleValue % players] += (marbleValue + next).toUInt()
            }
        }
        return scores.max()
    }

    override fun solve(input: PairOf<Int>): PairOf<UInt> {
        val (players, maxScore) = input
        return Pair(
            winningScore(players, maxScore),
            winningScore(players, maxScore * 100)
        )
    }
}