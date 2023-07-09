package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.PairOf
import adventOfCode.util.Point2D

object Solution11 : Solution<Int>(AOC_YEAR, 11) {
    private const val N = 300

    override fun getInput(handler: InputHandler): Int {
        return handler.getInput().toInt()
    }

    override fun solve(serialNumber: Int): PairOf<String> {
        val grid = MutableList(N) { MutableList(N) { 0 } }
        for (y in 1..N) {
            for (x in 1..N) {
                val rackId = x + 10
                var powerLevel = (rackId * y + serialNumber) * rackId
                powerLevel = (powerLevel / 100) % 10 - 5
                grid[y - 1][x - 1] = powerLevel
            }
        }
        var max3Power = (0..2).sumOf { y -> (0..2).sumOf { x -> grid[y][x]  } }
        var max3Corner = Point2D(0, 0)
        var maxPower = max3Power
        var maxCorner = Triple(0, 0, 3)
        for (yMin in 1..N) {
            for (xMin in 1..N) {
                var power = 0
                for ((yMax, xMax) in (yMin..N).zip(xMin..N)) {
                    power += (grid[yMax - 1][xMax - 1]
                            + (yMin until yMax).sumOf { grid[it - 1][xMax - 1] }
                            + (xMin until xMax).sumOf { grid[yMax - 1][it - 1] }
                            )
                    val size = yMax - yMin + 1
                    if (size == 3 && power > max3Power) {
                        max3Power = power
                        max3Corner = Point2D(xMin, yMin)
                    }
                    if (power > maxPower) {
                        maxPower = power
                        maxCorner = Triple(xMin, yMin, size)
                    }
                }
            }
        }
        return Pair(
            max3Corner.toList().joinToString(","),
            maxCorner.toList().joinToString(",")
        )
    }
}