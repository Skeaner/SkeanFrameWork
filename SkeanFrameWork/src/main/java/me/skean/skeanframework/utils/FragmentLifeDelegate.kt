package me.skean.skeanframework.utils

import android.app.Activity
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import me.skean.skeanframework.ktext.observe


class FragmentLifeDelegate<T : Any>(
    classes: Class<T>,
    fragment: Fragment
) : Lazy<T> {

    override val value: T
        get() = launcher
    private lateinit var launcher: T

    init {
        fragment.lifecycle.observe(create = {
            launcher = classes.getConstructor(Activity::class.java).newInstance(fragment)
        })
    }

    override fun isInitialized(): Boolean = this::launcher.isInitialized
}