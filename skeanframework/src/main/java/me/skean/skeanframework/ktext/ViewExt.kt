package me.skean.skeanframework.ktext

import android.annotation.SuppressLint
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.skean.skeanframework.delegate.DefaultTextWatcher
import kotlin.reflect.KMutableProperty

/**
 * Created by Skean on 19/11/5.
 */
fun ViewGroup.getAllChildViews(): MutableList<View> {
    var list = arrayListOf<View>()
    for (i in 0 until this.childCount) {
        list.add(this.getChildAt(i))
    }
    return list
}

fun ViewGroup.isAllChildChecked(): Boolean {
    for (cv in this.getAllChildViews()) {
        if (cv as? Checkable != null && !cv.isChecked) {
            return false
        }
    }
    return true
}

fun ViewGroup.isAnyChildChecked(): Boolean {
    for (cv in this.getAllChildViews()) {
        if (cv as? Checkable != null && cv.isChecked) {
            return true
        }
    }
    return false
}

fun RadioGroup.getChecedRadioButton(): RadioButton? {
    if (this.checkedRadioButtonId != -1) return findViewById(this.checkedRadioButtonId)
    return null
}

fun View.setVisibleOrGone(visibleOrGone: Boolean) {
    this.visibility = if (visibleOrGone) View.VISIBLE else View.GONE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun TextView.textOrBlankNull(): String? {
    return if (this.text.isNotBlank()) this.text.toString() else null
}

fun TextView.textOrBlank(): String {
    return if (this.text.isNotBlank()) this.text.toString() else ""
}

fun TextView.showSelectionsPopupMenu(selections: List<String>) {
    PopupMenu(this.context, this).also {
        val menu = it.menu
        selections.forEach { selection -> menu.add(selection) }
    }.also {
        it.setOnMenuItemClickListener { item ->
            this.text = item.title
            return@setOnMenuItemClickListener true
        }
    }.show()
}


fun View.showSelectionsPopupMenu(selections: List<String>, listener: PopupMenu.OnMenuItemClickListener?) {
    PopupMenu(this.context, this).also {
        val menu = it.menu
        selections.forEach { selection -> menu.add(selection) }
    }.also {
        it.setOnMenuItemClickListener(listener)
    }.show()
}

fun TextView.addTextChangedListenerAndSetValue(setter: (newValue: String) -> Unit) {
    this.addTextChangedListener(object : DefaultTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            setter.invoke(s.toString())
        }
    })
}


fun TextView.addTextChangedListenerAndSetValue(property: KMutableProperty<String?>) {
    this.addTextChangedListener(object : DefaultTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            property.setter.call(s.toString().ifBlank { null })
        }
    })
}


fun TextView.setDrawableTop(@DrawableRes drawableTop: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(0, drawableTop, 0, 0)
}


fun TextView.setDrawableLeft(@DrawableRes drawableLeft: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, 0, 0, 0)
}

fun TextView.setDrawableRight(@DrawableRes drawableRight: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRight, 0)
}

fun TextView.setDrawableBottom(@DrawableRes drawableBottom: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, drawableBottom)

}


@SuppressLint("RestrictedApi")
fun BottomNavigationView.disableShiftMode(){
    val menuView = this.getChildAt(0) as BottomNavigationMenuView
    try {
        val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
        shiftingMode.isAccessible = true
        shiftingMode.setBoolean(menuView, false)
        shiftingMode.isAccessible = false
        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i) as BottomNavigationItemView
            item.setShifting(false)
            item.setChecked(item.itemData.isChecked)
        }
    }
    catch (e: Exception) {
    }
}

