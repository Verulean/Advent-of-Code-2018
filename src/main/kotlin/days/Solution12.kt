package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.PairOf

private const val setChar = '#'
private const val unsetChar = '.'

private class CyclicScorer(
    states: Map<String, Pair<ULong, Long>>,
    private val cycleStart: ULong,
    private val cycleLength: ULong,
    private val cycleEndOffset: Long
) {
    private val generations: Map<ULong, Pair<String, Long>> = states
        .asSequence()
        .associate { it.value.first to Pair(it.key, it.value.second) }

    private val cycleStride = cycleEndOffset - generations.getValue(cycleStart).second

    private fun scoreState(state: String, offset: Long): Long {
        var plants = 0L
        var score = 0L
        for ((i, c) in state.withIndex()) {
            if (c == setChar) {
                plants++
                score += i
            }
        }
        return score + plants * offset
    }

    private fun rectifyGeneration(generation: ULong): Pair<ULong, ULong> {
        if (generations.contains(generation)) return 0UL to generation
        val generationsLeft = generation - cycleStart
        val fullCycles = generationsLeft / cycleLength
        val remainingCycles = generationsLeft % cycleLength
        return fullCycles to cycleStart + remainingCycles
    }

    fun score(generation: ULong): Long {
        if (generations.contains(generation)) {
            val (state, offset) = generations.getValue(generation)
            return scoreState(state, offset)
        }
        val (fullCycles, equivalentGeneration) = rectifyGeneration(generation)
        val (state, offset) = generations.getValue(equivalentGeneration)
        val realOffset = offset + fullCycles.toLong() * cycleStride
        return scoreState(state, realOffset)
    }
}

object Solution12: Solution<Pair<String, Map<String, Char>>>(AOC_YEAR, 12) {
    private var rules: Map<String, Char> = mapOf()
    private var sightDistance = 0

    private fun getBits(input: String) = input.filter { it == setChar || it == unsetChar }

    override fun getInput(handler: InputHandler): Pair<String, Map<String, Char>> {
        val (firstLine, ruleString) = handler.getInput("\n\n")
        val initialState = getBits(firstLine)
        val rules = ruleString
            .split("\n")
            .map { it.split(" => ") }
            .filter { it.size == 2 }
            .associate { getBits(it[0]) to getBits(it[1]).first() }
        return Pair(initialState, rules)
    }

    private fun normalizeState(state: String): Pair<String, Long> {
        var builder = StringBuilder(state)

        val start = 2 * sightDistance - state.takeWhile(unsetChar::equals).length
        builder = if (start > 0) builder.insert(0, unsetChar.toString().repeat(start))
        else builder.delete(0, -start)

        val end = 2 * sightDistance - state.takeLastWhile(unsetChar::equals).length
        builder = if (end > 0) builder.append(unsetChar.toString().repeat(end))
        else builder.delete(builder.length + end, builder.length)

        return Pair(builder.toString(), -start.toLong())
    }

    private fun step(state: String): String {
        val builder = StringBuilder(state)
        for (i in sightDistance until state.length - sightDistance) {
            val key = state.slice(i - sightDistance..i + sightDistance)
            builder[i] = rules.getOrDefault(key, state[i])
        }
        return builder.toString()
    }

    override fun solve(input: Pair<String, Map<String, Char>>): PairOf<Long> {
        rules = input.second
        sightDistance = rules.keys.first().length / 2

        var generation = 0UL
        var (state, offset) = normalizeState(input.first)
        val seenStates = mutableMapOf<String, Pair<ULong, Long>>()
        while (true) {
            if (seenStates.contains(state)) {
                val (startGeneration, startOffset) = seenStates.getValue(state)
                val scorer = CyclicScorer(
                    seenStates,
                    startGeneration,
                    generation - startGeneration,
                    offset
                )
                return Pair(scorer.score(20UL), scorer.score(50_000_000_000UL))
            }
            seenStates[state] = Pair(generation, offset)
            state = step(state)
            val (paddedState, padOffset) = normalizeState(state)
            offset += padOffset
            state = paddedState
            generation++
        }
    }
}