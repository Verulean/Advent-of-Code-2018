package adventOfCode.util

private fun findMatches(input: CharSequence, pattern: String): List<String> {
    return Regex(pattern).findAll(input).map(MatchResult::value).toList()
}

fun CharSequence.ints(allowNegatives: Boolean = true): List<Int> {
    val pattern = if (allowNegatives) "-?\\d+" else "\\d+"
    return findMatches(this, pattern).map(String::toInt)
}

fun CharSequence.doubles(allowNegatives: Boolean = true): List<Double> {
    val pattern = if (allowNegatives) "-?\\d+(?:\\.\\d+)?" else "\\d+(?:\\.\\d+)?"
    return findMatches(this, pattern).map(String::toDouble)
}

fun CharSequence.words(): List<String> {
    val pattern = "[A-Za-z]+"
    return findMatches(this, pattern)
}