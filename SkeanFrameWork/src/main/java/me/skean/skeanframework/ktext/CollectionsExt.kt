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

fun <T> List<T>?.orEmptyMutableiList(): MutableList<T> = this?.toMutableList() ?: mutableListOf()

