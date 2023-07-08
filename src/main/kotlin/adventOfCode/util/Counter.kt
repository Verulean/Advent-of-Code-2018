package adventOfCode.util

open class Counter<E>(collection: Iterable<E> = emptyList()) : DefaultHashMap<E, Int>({ 0 }) {
    init { collection.forEach { this[it]++ } }
}
