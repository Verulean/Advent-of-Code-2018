package days

import adventOfCode.InputHandler
import adventOfCode.Solution
import adventOfCode.util.DefaultHashMap
import adventOfCode.util.ints

data class DateTime(val year: Int, val month: Int, val day: Int, val hour: Int, val minute: Int) : Comparable<DateTime> {
    override operator fun compareTo(other: DateTime): Int {
        return when {
            year != other.year -> year - other.year
            month != other.month -> month - other.month
            day != other.day -> day - other.day
            hour != other.hour -> hour - other.hour
            minute != other.minute -> minute - other.minute
            else -> 0
        }
    }

    fun sameDay(other: DateTime): Boolean {
        return year == other.year
                && month == other.month
                && day == other.day
    }
}

enum class GuardEventType {
    START, SLEEP, WAKE
}

data class GuardEvent(val instant: DateTime, val type: GuardEventType, val guardId: Int? = null) : Comparable<GuardEvent> {
    override operator fun compareTo(other: GuardEvent): Int {
        return instant.compareTo(other.instant)
    }
}

object Solution04 : Solution<List<GuardEvent>>(AOC_YEAR, 4) {
    override fun getInput(handler: InputHandler): List<GuardEvent> {
        val records = handler.getInput("\n")
        val events = mutableListOf<GuardEvent>()
        for (line in records) {
            val numbers = line.ints(false)
            val (year, month, day, hour, minute) = numbers
            val instant = DateTime(year, month, day, hour, minute)
            if (numbers.size > 5) {
                events.add(GuardEvent(instant, GuardEventType.START, numbers[5]))
            } else {
                val type = if (line.endsWith("falls asleep")) GuardEventType.SLEEP else GuardEventType.WAKE
                events.add(GuardEvent(instant, type))
            }
        }
        events.sort()
        return events
    }

    private fun getSleepMetrics(sleepTimes: HashMap<Int, Int>): Triple<Int, Int, Int> {
        val totalSleep = sleepTimes.values.sum()
        val (maxMinute, maxTimesSlept) = sleepTimes.maxBy { it.value }
        return Triple(totalSleep, maxTimesSlept, maxMinute)
    }

    override fun solve(input: List<GuardEvent>): Pair<Int, Int> {
        // Part 1
        val guardSleepTimes = DefaultHashMap<Int, DefaultHashMap<Int, Int>> { DefaultHashMap { 0 } }
        var (lastInstant, _, currentId) = input[0]
        var lastEdge = 0
        var isAsleep = false
        for ((instant, type, guardId) in input) {
            val end = if (instant.sameDay(lastInstant) && instant.hour == 0) instant.minute else 60
            when (type) {
                GuardEventType.START -> {
                    if (isAsleep) {
                        for (minute in lastEdge until end) {
                            guardSleepTimes[currentId!!][minute]++
                        }
                        isAsleep = false
                    }
                    currentId = guardId
                }
                GuardEventType.WAKE -> {
                    if (isAsleep) {
                        for (minute in lastEdge until end) {
                            guardSleepTimes[currentId!!][minute]++
                        }
                        isAsleep = false
                    }
                }
                else -> isAsleep = true
            }
            lastInstant = instant
            lastEdge = if (instant.hour == 0) instant.minute else 0
        }
        val sleepNumbers = guardSleepTimes.mapValues { getSleepMetrics(it.value) }
        val sleepiestGuard = sleepNumbers.maxBy { it.value.first }
        val ans1 = sleepiestGuard.key * sleepiestGuard.value.third
        // Part 2
        val eepiestGuard = sleepNumbers.maxBy { it.value.second }
        val ans2 = eepiestGuard.key * eepiestGuard.value.third
        return Pair(ans1, ans2)
    }
}