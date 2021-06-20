package pro.darc.cake.module.extensions

import kotlin.random.Random

fun Collection<String>.containsIgnoreCase(
    element: String
): Boolean = any { it.equals(element, true) }

fun <T> MutableCollection<T>.clear(onRemove: (T) -> Unit = {}) {
    toMutableList().forEach {
        remove(it)
        onRemove(it)
    }
}

fun Array<String>.containsIgnoreCase(
    element: String
): Boolean = any { it.equals(element, true) }

fun <V> Map<String, V>.containsKeyIgnoreCase(
    key: String
): Boolean = keys.containsIgnoreCase(key)

fun <V> Map<String, V>.getIgnoreCase(
    key: String
): V? = entries.find { it.key.equals(key, true) }?.value

fun <K, V> MutableMap<K, V>.clear(onRemove: (K, V) -> Unit) {
    keys.toMutableSet().forEach { onRemove(it, remove(it)!!) }
}

fun <E> Collection<E>.randomOrNull(): E? = runCatching { random() }.getOrNull()
fun <E> Collection<E>.randomIndex(): Int = if(size > 0) Random.nextInt(size) else -1

fun <E> Collection<E>.randomize(): MutableList<E> {
    val oldList = toMutableList()
    val newList = mutableListOf<E>()

    for(i in 0 until size) {
        val index = oldList.randomIndex()
        newList.add(oldList[index])
        oldList.removeAt(index)
    }

    return newList
}
