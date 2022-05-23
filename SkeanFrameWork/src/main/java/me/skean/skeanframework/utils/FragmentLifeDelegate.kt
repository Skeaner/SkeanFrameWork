package me.skean.skeanframework.utils

import android.app.Activity
import androidx.fragment.app.Fragment
import me.skean.skeanframework.ktext.listOrderedMapOf
import me.skean.skeanframework.ktext.observe
import org.apache.commons.lang3.ClassUtils


class FragmentLifeDelegate<T : Any>(
    clazz: Class<T>,
    fragment: Fragment,
    parameters: Array<out Pair<String, Any?>>,
    construct: (fragment: Fragment, parameters: Map<String, Any?>) -> T?,
    init: (T) -> Unit
) : Lazy<T> {

    override val value: T
        get() = instance
    private lateinit var instance: T

    init {
        fragment.lifecycle.observe(create = {
            buildInstance(clazz, fragment, parameters, construct, init)
        })
    }

    private fun buildInstance(
        clazz: Class<T>,
        fragment: Fragment,
        parameters: Array<out Pair<String, Any?>> = arrayOf(),
        construct: (fragment: Fragment, parameters: Map<String, Any?>) -> T? = { _, _ -> null },
        init: (T) -> Unit
    ): T {
        val parametersMap = listOrderedMapOf(*parameters)
        val instance = construct(fragment, parametersMap)
        if (instance != null) {
            this.instance = instance
        } else {
            for (constructor in clazz.constructors) {
                val parameterSize = constructor.parameterTypes.size
                if (parameterSize == 1 + parametersMap.size) {
                    var allTypeMatch = true
                    val params = arrayOfNulls<Any?>(parameterSize)
                    constructor.parameterTypes.forEachIndexed { index, clazz ->
                        if (index == 0) {
                            var isTypeMatch = false
                            if (clazz.isInstance(fragment)) {
                                params[0] = fragment
                                isTypeMatch = true
                            } else if (clazz.isInstance(fragment.requireActivity())) {
                                params[0] = fragment.requireActivity()
                                isTypeMatch = true
                            }
                            allTypeMatch = allTypeMatch and isTypeMatch
                        } else {
                            val param: Any? = parametersMap.getValue(index - 1)
                            val validateClass = if (clazz.isPrimitive) ClassUtils.primitiveToWrapper(clazz) else clazz
                            params[index] = param
                            allTypeMatch = allTypeMatch and validateClass.isInstance(param)
                        }
                    }
                    if (allTypeMatch) {
                        this.instance = constructor.newInstance(*params) as T
                        break
                    }
                }
            }
        }
        if (isInitialized()) {
            init(this.instance)
        } else {
            throw RuntimeException("没有对应的构造方法")
        }
        return this.instance
    }


    override fun isInitialized(): Boolean = this::instance.isInitialized
}