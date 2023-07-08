package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.Counter
import adventOfCode.util.ints
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

private typealias Point = Pair<Int, Int>

private fun <T> Sequence<T>.identical(): Boolean {
    val first: T
    try {
        first = this.first()
    } catch (_: NoSuchElementException) {
        return true
    }
    return this.all { it == first }
}

object Solution06 : Solution<List<Point>>(AOC_YEAR, 6) {
    private val directions = arrayOf(Point(1, 0), Point(0, 1), Point(-1, 0), Point(0, -1))
    private val safeDistance = 10_000

    override fun getInput(handler: InputHandler): List<Point> {
        return handler.getInput("\n") { line ->
            line.ints { Pair(it[0], it[1]) }
        }
    }

    private fun parity(u1: Int, u2: Int, v1: Int, v2: Int): Int {
        val z = u1 * v2 - u2 * v1
        return z.sign
    }

    private fun onBoundary(p1: Point, p2: Point, points: List<Point>): Boolean {
        val (x1, y1) = p1
        val (x2, y2) = p2
        val u1 = x2 - x1
        val u2 = y2 - y1
        return points
            .asSequence()
            .filter { p -> p != p1 && p != p2 }
            .map { p -> parity(u1, u2, p.first - x1, p.second - y1) }
            .identical()
    }

    private fun manhattan(x1: Int, y1: Int, x2: Int, y2: Int): Int {
        return (x2 - x1).absoluteValue + (y2 - y1).absoluteValue
    }

    private fun manhattan(p1: Point, p2: Point): Int {
        val (x1, y1) = p1
        val (x2, y2) = p2
        return manhattan(x1, y1, x2, y2)
    }

    private fun boundingRect(points: Set<Point>, padding: Int = 0): List<Int> {
        val (x0, y0) = points.first()
        var xMin = x0
        var xMax = x0
        var yMin = y0
        var yMax = y0
        for ((x, y) in points) {
            xMin = min(xMin, x)
            xMax = max(xMax, x)
            yMin = min(yMin, y)
            yMax = max(yMax, y)
        }
        return listOf(xMin - padding, xMax + padding, yMin - padding, yMax + padding)
    }

    private fun uniqueMinOrNull(distances: Map<Point, Int>): Point? {
        if (distances.isEmpty()) return null
        val first = distances.entries.first()
        var minPoint: Point? = first.key
        var minDist = first.value
        for ((point, dist) in distances) {
            when {
                point == minPoint -> continue
                dist == minDist -> {
                    minPoint = null
                }
                dist < minDist -> {
                    minDist = dist
                    minPoint = point
                }
            }
        }
        return minPoint
    }

    private fun buildNearestMap(interior: MutableSet<Point>, points: Set<Point>, padding: Int = 0): Pair<HashMap<Point, Point?>, Point?> {
        val (xMin, xMax, yMin, yMax) = boundingRect(points, padding)
        var safeSeed: Point? = null
        val start = interior.first()
        val nearestPoint = HashMap<Point, Point?>()
        nearestPoint[start] = start
        val queue = mutableSetOf(start)
        while (queue.size > 0) {
            val point = queue.first()
            val (x, y) = point
            queue.remove(point)
            val pointDistances = points.associateWith { manhattan(point, it) }
            if (safeSeed == null && pointDistances.values.sum() < safeDistance) safeSeed = point
            val currNearest = uniqueMinOrNull(pointDistances)
            nearestPoint[point] = currNearest
            if (!interior.contains(currNearest)) continue
            for ((dx, dy) in directions) {
                val xx = x + dx
                val yy = y + dy
                if (xx !in xMin..xMax || yy !in yMin..yMax) {
                    interior.remove(currNearest)
                    continue
                }
                val nextPoint = Pair(xx, yy)
                if (!nearestPoint.containsKey(nextPoint)) queue.add(nextPoint)
            }
        }
        return Pair(nearestPoint, safeSeed)
    }

    private fun safeArea(points: Set<Point>, start: Point?): Int {
        if (start == null) return 0
        val safeRegion = mutableSetOf(start)
        val queue = mutableSetOf(start)
        while (queue.size > 0) {
            val point = queue.first()
            val (x, y) = point
            queue.remove(point)
            if (points.associateWith { manhattan(point, it) } .values.sum() >= safeDistance) continue
            safeRegion.add(point)
            for ((dx, dy) in directions) {
                val other = Pair(x + dx, y + dy)
                if (safeRegion.contains(other)) continue
                queue.add(other)
            }
        }
        return safeRegion.size
    }

    override fun solve(input: List<Point>): Pair<Any?, Any?> {
        val points = input.toSet()
        val n = input.size
        val interior = input.toMutableSet()
        for ((i, p1) in input.withIndex()) {
            if (!interior.contains(p1)) continue
            for (j in i + 1 until n) {
                val p2 = input[j]
                if (onBoundary(p1, p2, input)) {
                    interior.remove(p1)
                    interior.remove(p2)
                }
            }
        }
        val (nearestPoints, safeSeed) = buildNearestMap(interior, points)
        val pointAreas = Counter(nearestPoints.values)
        val ans1 = interior.maxOf { pointAreas[it] }
        return Pair(ans1, safeArea(points, safeSeed))
    }
}