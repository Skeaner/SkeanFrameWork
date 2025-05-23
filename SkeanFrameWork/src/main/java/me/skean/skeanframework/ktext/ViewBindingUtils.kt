@file:JvmName("ViewBindingUtils")
package me.skean.skeanframework.ktext

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter4.BaseQuickAdapter
import java.lang.reflect.ParameterizedType


@JvmName("inflateBinding")
fun <VB : ViewBinding> BaseQuickAdapter<*, *>.inflateBinding(
    vbClass: Class<VB>,
    context: Context,
    parent: ViewGroup?
): VB {
    val vb = vbClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        .invoke(null, LayoutInflater.from(context), parent, false) as VB
    if (vb is ViewDataBinding) {
        vb.lifecycleOwner = this.recyclerView.findViewTreeLifecycleOwner()
    }
    return vb
}

fun <VB : ViewBinding> queryGenericBindingClass(any: Any, genericIndex: Int = 0): Class<VB> {
    var clazz = any.javaClass
    while (clazz != null) {
        val genericSuperclass = clazz.genericSuperclass
        if (genericSuperclass is ParameterizedType) {
            try {
                val types = genericSuperclass.actualTypeArguments
                return types[genericIndex] as Class<VB>
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        clazz = clazz.superclass
    }
    throw IllegalArgumentException("There is no generic of ViewBinding.")
}
