package adventOfCode.util

open class DefaultHashMap<K, V>(val factory: () -> V) : HashMap<K, V>() {
    override operator fun get(key: K): V {
        if (!super.containsKey(key)) set(key, factory())
        return super.get(key)!!
    }
}