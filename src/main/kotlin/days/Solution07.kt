package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.DefaultHashMap
import kotlin.math.min

private class Step {
    var parents: MutableList<String> = mutableListOf()
}

private class WorkQueue(private val workers: Int) {
    private val queue: MutableList<Pair<String, Int>?> = MutableList(workers) { null }

    val freeWorkers
        get() = queue.count {it == null}

    val activeSteps
        get() = queue.filterNotNull().map(Pair<String, Int>::first).toSet()

    fun enqueue(step: String, remainingTime: Int): Boolean {
        for ((i, worker) in queue.withIndex()) {
            if (worker != null) continue
            queue[i] = Pair(step, remainingTime)
            return true
        }
        return false
    }

    fun wait(): Pair<Int, List<String>> {
        val t = queue.filterNotNull().minOf { it.second }
        val done = mutableListOf<String>()
        for ((i, worker) in queue.withIndex()) {
            when {
                worker == null -> continue
                worker.second <= t -> {
                    done.add(worker.first)
                    queue[i] = null
                }
                else -> {
                    queue[i] = Pair(worker.first, worker.second - t)
                }
            }
        }
        return Pair(t, done.sorted())
    }
}

object Solution07 : Solution<List<List<String>>>(AOC_YEAR, 7) {
    private const val workers = 5

    override fun getInput(handler: InputHandler): List<List<String>> {
        return handler.getInput("\n") {
            Regex("Step ([A-Z]) must be finished before step ([A-Z]) can begin\\.")
                .find(it)!!
                .groups
                .filterNotNull()
                .map(MatchGroup::value)
                .slice(1..2)
        }
    }

    private fun executionTime(id: String): Int {
        return 61 + (id[0] - 'A')
    }

    override fun solve(input: List<List<String>>): Pair<String, Int> {
        val steps = DefaultHashMap<String, Step> { Step() }
        for ((source, dest) in input) {
            val sourceStep = steps[source]
            val destStep = steps[dest]
            destStep.parents.add(source)
        }
        val stepIds = steps.keys.toSet()

        // Part 1
        var ans1 = ""
        val done = mutableSetOf<String>()
        while (done != stepIds) {
            val readySteps = mutableSetOf<String>()
            for ((id, step) in steps.filter { !done.contains(it.key) }) {
                if (step.parents.all { done.contains(it) }) {
                    readySteps.add(id)
                }
            }
            val nextStep = readySteps.min()
            ans1 += nextStep
            done.add(nextStep)
        }

        // Part 2
        done.clear()
        val workQueue = WorkQueue(workers)
        var t = 0
        while (done != stepIds) {
            val readySteps = mutableListOf<String>()
            val activeSteps = workQueue.activeSteps
            for ((id, step) in steps.filter { !done.contains(it.key) }) {
                if (!activeSteps.contains(id) && step.parents.all { done.contains(it) }) {
                    readySteps.add(id)
                }
            }
            readySteps.sort()
            val stepsToEnqueue = min(readySteps.size, workQueue.freeWorkers)
            if (stepsToEnqueue > 0) {
                for (step in readySteps.slice(0 until stepsToEnqueue)) {
                    workQueue.enqueue(step, executionTime(step))
                }
            } else {
                val (dt, newDone) = workQueue.wait()
                t += dt
                done += newDone
            }
        }
        return Pair(ans1, t)
    }
}