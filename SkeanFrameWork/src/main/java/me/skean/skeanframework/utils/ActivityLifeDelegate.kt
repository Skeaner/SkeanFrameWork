package me.skean.skeanframework.utils

import android.app.Activity
import android.os.Build
import androidx.activity.ComponentActivity
import me.skean.skeanframework.ktext.listOrderedMapOf
import me.skean.skeanframework.ktext.observe
import org.apache.commons.lang3.ClassUtils


class ActivityLifeDelegate<T : Any>(
    clazz: Class<T>,
    activity: Activity,
    parameters: Array<out Pair<String, Any?>>,
    construct: (activity: Activity, parameters: Map<String, Any?>) -> T?,
    init: (T) -> Unit
) : Lazy<T> {

    override val value: T
        get() = instance
    private lateinit var instance: T

    init {
        when (activity) {
            is ComponentActivity -> activity.lifecycle.observe(create = {
                buildInstance(clazz, activity, parameters, construct, init)
            })
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    activity.observe(create = {
                        buildInstance(clazz, activity, parameters, construct, init)
                    })
                }
            }
        }
    }

    private fun buildInstance(
        clazz: Class<T>,
        activity: Activity,
        parameters: Array<out Pair<String, Any?>> = arrayOf(),
        construct: (activity: Activity, parameters: Map<String, Any?>) -> T? = { _, _ -> null },
        init: (T) -> Unit
    ): T {
        val parametersMap = listOrderedMapOf(*parameters)
        val instance = construct(activity, parametersMap)
        if (instance != null) {
            this.instance = instance
        } else {
            for (constructor in clazz.constructors) {
                val parameterSize = constructor.parameterTypes.size
                if (parameterSize == 1 + parametersMap.size) {
                    var allTypeMatch = true
                    val params = arrayOfNulls<Any?>(parameterSize)
                    constructor.parameterTypes.forEachIndexed { index, clazz ->
                        val param: Any? = if (index == 0) activity else parametersMap.getValue(index - 1)
                        params[index] = param
                        val validateClass = if (clazz.isPrimitive) ClassUtils.primitiveToWrapper(clazz) else clazz
                        allTypeMatch = allTypeMatch and validateClass.isInstance(param)
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