package me.skean.skeanframework.ktext

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment

/**
 * Created by Skean on 2022/4/27.
 */
inline fun <reified T : Activity> Activity.intent(): Intent {
    return Intent(this, T::class.java)
}

inline fun <reified T : Activity> Fragment.intent(): Intent {
    return Intent(requireContext(), T::class.java)
}