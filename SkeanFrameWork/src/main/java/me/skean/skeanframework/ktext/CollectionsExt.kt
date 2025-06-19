package me.skean.skeanframework.ktext

import org.apache.commons.collections4.map.ListOrderedMap

/**
 * Created by Skean on 20/6/8.
 */
fun <K, V> listOrderedMapOf(vararg pairs: Pair<K, V>): ListOrderedMap<K, V> {
    val map = ListOrderedMap<K, V>()
    map.putAll(pairs)
    return map
}

fun <T> List<T>?.toMutableListOrEmpty(): MutableList<T> = this?.toMutableList() ?: mutableListOf()

inline fun <reified T> List<T>?.toArrayOrEmpty(): Array<T> = this?.toTypedArray() ?: arrayOf()

fun <T> List<T>.toMutableListAddAll(elements: Collection<T>) = this.toMutableList().apply { addAll(elements) }

fun <T> List<T>.toMutableListAddAll(elements: Iterable<T>) = this.toMutableList().apply { addAll(elements) }

fun <T> List<T>.toMutableListAddAll(elements: Sequence<T>) = this.toMutableList().apply { addAll(elements) }

fun <T> List<T>.toMutableListAddAll(elements: Array<out T>) = this.toMutableList().apply { addAll(elements) }