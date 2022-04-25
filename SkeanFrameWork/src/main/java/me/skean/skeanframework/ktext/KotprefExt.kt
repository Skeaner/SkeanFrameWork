package me.skean.skeanframework.ktext

import android.util.Property
import com.chibatching.kotpref.KotprefModel
import kotlin.reflect.KProperty

/**
 * Created by Skean on 21/6/3.
 */
fun KotprefModel.contains(property: KProperty<*>):Boolean{
    return preferences.contains(getPrefKey(property))
}