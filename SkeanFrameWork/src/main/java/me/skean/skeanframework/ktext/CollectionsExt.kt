package me.skean.skeanframework.ktext

import org.apache.commons.collections4.map.ListOrderedMap
import java.util.Collections

/**
 * Created by Skean on 20/6/8.
 */
fun <K, V> listOrderedMapOf(vararg pairs: Pair<K, V>): ListOrderedMap<K, V> {
    val map = ListOrderedMap<K, V>()
    map.putAll(pairs)
    return map
}

fun <T> List<T>?.orEmptyMutableList(): MutableList<T> = this?.toMutableList() ?: mutableListOf()

fun <T> List<T>.toMutableListAndAll(elements: Collection<T>) = this.toMutableList().apply { addAll(elements) }

fun <T> List<T>.toMutableListAndAll(elements: Iterable<T>) = this.toMutableList().apply { addAll(elements) }

fun <T> List<T>.toMutableListAndAll(elements: Sequence<T>) = this.toMutableList().apply { addAll(elements) }

fun <T> List<T>.toMutableListAndAll(elements: Array<out T>) = this.toMutableList().apply { addAll(elements) }