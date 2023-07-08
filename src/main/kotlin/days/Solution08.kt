package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.ints

private data class Node(
    val parent: Node? = null,
    val children: MutableList<Node> = mutableListOf(),
    val metadata: MutableList<Int> = mutableListOf()
) {
    val value: Int
        get() {
            return if (children.size == 0) metadata.sum() else
                metadata
                    .asSequence()
                    .map(Int::dec)
                    .filter(children.indices::contains)
                    .map(children::get)
                    .map(Node::value)
                    .sum()
        }
}

object Solution08 : Solution<List<Int>>(AOC_YEAR, 8) {
    private var metadataSum = 0

    override fun getInput(handler: InputHandler): List<Int> {
        return handler.getInput().ints()
    }

    private fun buildNode(buffer: MutableList<Int>, parent: Node? = null): Node {
        val childCount = buffer.removeFirst()
        val metadataCount = buffer.removeFirst()
        var node = Node(parent)
        repeat(childCount) {
            node.children.add(buildNode(buffer, node))
        }
        repeat(metadataCount) {
            val metadataValue = buffer.removeFirst()
            node.metadata.add(metadataValue)
            metadataSum += metadataValue
        }
        return node
    }

    override fun solve(input: List<Int>): Pair<Int, Int> {
        val buffer = input.toMutableList()
        val root = buildNode(buffer)
        return Pair(metadataSum, root.value)
    }
}