package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.PairOf
import adventOfCode.util.Point2D
import adventOfCode.util.plus

private val UP = Point2D(-1, 0)
private val DOWN = Point2D(1, 0)
private val LEFT = Point2D(0, -1)
private val RIGHT = Point2D(0, 1)

private val indexToDirection = arrayOf(DOWN, LEFT, UP, RIGHT)

private val directionToIndex = mapOf(DOWN to 0, LEFT to 1, UP to 2, RIGHT to 3)

private fun rotate(vector: Point2D, clockwiseTurns: Int = 1): Point2D {
    var index = (directionToIndex[vector]!! + clockwiseTurns) % 4
    if (index < 0) index += 4
    return indexToDirection[index]
}

enum class TrackType {
    INTERSECTION,
    HORIZONTAL,
    VERTICAL,
    CORNER_UP_RIGHT,
    CORNER_DOWN_RIGHT
}

private val charToTrack = mapOf(
    '+' to TrackType.INTERSECTION,
    '-' to TrackType.HORIZONTAL,
    '>' to TrackType.HORIZONTAL,
    '<' to TrackType.HORIZONTAL,
    '|' to TrackType.VERTICAL,
    '^' to TrackType.VERTICAL,
    'v' to TrackType.VERTICAL,
    '/' to TrackType.CORNER_UP_RIGHT,
    '\\' to TrackType.CORNER_DOWN_RIGHT
)

private val cartDirection = mapOf(
    '^' to UP,
    'v' to DOWN,
    '<' to LEFT,
    '>' to RIGHT
)

class Cart(var position: Point2D, private var velocity: Point2D, private val tracks: Map<Point2D, TrackType>) : Comparable<Cart> {
    private var intersectionChoice = 0
    var crashed = false

    val positionString
        get() = "${position.second},${position.first}"

    override fun compareTo(other: Cart): Int {
        val (i1, j1) = position
        val (i2, j2) = other.position
        return if (i1 != i2) i1 - i2 else j1 - j2
    }

    private fun turnForCorner() {
        velocity = when (Pair(velocity, tracks[position])) {
            UP to TrackType.CORNER_UP_RIGHT -> RIGHT
            DOWN to TrackType.CORNER_UP_RIGHT -> LEFT
            LEFT to TrackType.CORNER_UP_RIGHT -> DOWN
            RIGHT to TrackType.CORNER_UP_RIGHT -> UP

            UP to TrackType.CORNER_DOWN_RIGHT -> LEFT
            DOWN to TrackType.CORNER_DOWN_RIGHT -> RIGHT
            LEFT to TrackType.CORNER_DOWN_RIGHT -> UP
            RIGHT to TrackType.CORNER_DOWN_RIGHT -> DOWN

            else -> velocity
        }
    }

    private fun turnForIntersection() {
        when (intersectionChoice) {
            0 -> velocity = rotate(velocity, -1)
            2 -> velocity = rotate(velocity, 1)
        }
        intersectionChoice = (intersectionChoice + 1) % 3
    }

    fun move() {
        if (crashed) return
        position += velocity
        when (tracks[position]) {
            TrackType.INTERSECTION -> turnForIntersection()
            TrackType.CORNER_UP_RIGHT, TrackType.CORNER_DOWN_RIGHT -> turnForCorner()
            else -> {}
        }
    }
}

object Solution13 : Solution<List<Cart>>(AOC_YEAR, 13) {
    override fun getInput(handler: InputHandler): List<Cart> {
        val lines = handler.getInput("\n", trim = false)
        val tracks = mutableMapOf<Point2D, TrackType>()
        val cartData = mutableListOf<PairOf<Point2D>>()
        for ((i, line) in lines.withIndex()) {
            for ((j, char) in line.withIndex()) {
                val position = Point2D(i, j)
                val trackType = charToTrack.getOrDefault(char, null)
                if (trackType != null) {
                    tracks[position] = trackType
                }
                val cartFacing = cartDirection.getOrDefault(char, null)
                if (cartFacing != null) {
                    cartData.add(Pair(position, cartFacing))
                }
            }
        }
        return cartData.map {
            Cart(it.first, it.second, tracks)
        }
    }

    override fun solve(input: List<Cart>): Pair<Any?, Any?> {
        val carts = input.toMutableList()
        var ans1: String? = null
        while (true) {
            if (carts.size == 1) {
                val lastCart = carts.first()
                return Pair(ans1, lastCart.positionString)
            }
            var cartMoved = false
            for ((i, cart) in carts.withIndex()) {
                if (cart.crashed) continue
                cart.move()
                cartMoved = true
                val cartPosition = cart.position
                for ((_, otherCart) in carts.withIndex().filter { it.index != i && !it.value.crashed }) {
                    if (otherCart.position == cartPosition) {
                        cart.crashed = true
                        otherCart.crashed = true
                        if (ans1 == null) ans1 = cart.positionString
                    }
                }
            }
            carts.removeIf(Cart::crashed)
            carts.sort()
            if (!cartMoved) break
        }
        return Pair(ans1, null)
    }
}