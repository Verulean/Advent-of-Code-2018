package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.PairOf
import adventOfCode.util.Point2D
import adventOfCode.util.plus
import adventOfCode.util.ints
import kotlin.math.max
import kotlin.math.min

object Solution10 : Solution<List<List<Int>>>(AOC_YEAR, 10) {
    override fun getInput(handler: InputHandler): List<List<Int>> {
        return handler.getInput("\n", transform = String::ints)
    }

    private fun getBounds(positions: List<Point2D>): List<Int> {
        val (x0, y0) = positions.first()
        var xMin = x0
        var xMax = x0
        var yMin = y0
        var yMax = y0
        for ((x, y) in positions) {
            xMin = min(xMin, x)
            xMax = max(xMax, x)
            yMin = min(yMin, y)
            yMax = max(yMax, y)
        }
        return listOf(xMin, xMax, yMin, yMax)
    }

    private fun getHeight(positions: List<Point2D>): Int {
        val (_, _, yMin, yMax) = getBounds(positions)
        return yMax - yMin + 1
    }

    private fun getField(positions: List<Point2D>, dark: Char = '.', light: Char = '#'): String {
        val (xMin, xMax, yMin, yMax) = getBounds(positions)
        val points = positions.toSet()
        return (yMin..yMax).map { y ->
            (xMin..xMax).map { x ->
                if (points.contains(Point2D(x, y))) light else dark
            } .joinToString("")
        } .joinToString("\n")
    }

    private fun step(positions: List<Point2D>, velocities: List<PairOf<Int>>): List<Point2D> {
        return positions.zip(velocities).map { it.first + it.second }
    }

    override fun solve(input: List<List<Int>>): Pair<String, Int> {
        var positions = mutableListOf<Point2D>()
        var oldPositions: MutableList<Point2D>
        val velocities = mutableListOf<PairOf<Int>>()
        for ((x, y, vx, vy) in input) {
            positions.add(Point2D(x, y))
            velocities.add(Pair(vx, vy))
        }
        var currentArea = getHeight(positions)
        var t = 0
        while (true) {
            oldPositions = positions
            positions = step(positions, velocities).toMutableList()
            val newArea = getHeight(positions)
            if (newArea > currentArea) {
                return Pair(getField(oldPositions), t)
            }
            currentArea = newArea
            t++
        }
    }
}