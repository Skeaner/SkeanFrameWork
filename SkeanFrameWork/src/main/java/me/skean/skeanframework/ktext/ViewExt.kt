@file:JvmName("ViewExt")

package me.skean.skeanframework.ktext

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.R
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.bundle.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.ActivityNavigator
import androidx.navigation.FloatingWindow
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.viewpager.widget.ViewPager
import com.amap.api.maps.MapView
import com.blankj.utilcode.util.ReflectUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxbinding4.view.clicks
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnMultiListener
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener
import me.skean.skeanframework.component.SkeanFrameWork
import me.skean.skeanframework.delegate.DefaultTextWatcher
import me.skean.skeanframework.delegate.OnMultiListenerWrapper
import me.skean.skeanframework.model.AppResponse
import okhttp3.HttpUrl
import org.apache.commons.collections4.map.ListOrderedMap
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
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

fun View.setGone(isGone: Boolean) {
    this.visibility = if (isGone) View.GONE else View.VISIBLE
}

fun View.setInvisible(isInvisible: Boolean) {
    this.visibility = if (isInvisible) View.INVISIBLE else View.VISIBLE
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun View.isGone(): Boolean {
    return this.visibility == View.GONE
}

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.isInvisible(): Boolean {
    return this.visibility == View.INVISIBLE
}

fun View.setOnClickFilterListener(onClick: ((t: Any) -> Unit)) {
    this.clicks()
        .throttleFirst(1, TimeUnit.SECONDS)
        .subscribe(onClick)
}

fun View.setOnClickFilterListener(millis: Long, onClick: ((t: Any) -> Unit)) {
    this.clicks()
        .throttleFirst(millis, TimeUnit.MILLISECONDS)
        .subscribe(onClick)
}

fun <T : View> T.postAction(action: (T) -> Unit) {
    this.post {
        action(this)
    }
}

fun TextView.textOrBlankNull(): String? {
    return if (this.text.isNotBlank()) this.text.toString() else null
}

fun TextView.textOrBlank(): String {
    return if (this.text.isNotBlank()) this.text.toString() else ""
}


fun TextView.showSelectionsPopupMenu(vararg selections: String) {
    this.showSelectionsPopupMenu(selections.toList())
}

fun TextView.showSelectionsPopupMenuOnClick(vararg selections: String) {
    this.setOnClickListener {
        this.showSelectionsPopupMenu(selections.toList())
    }
}


fun TextView.showSelectionsPopupMenu(selections: List<String>) {
    PopupMenu(this.context, this).also {
        val menu = it.menu
        selections.forEachIndexed { index, selection ->
            menu.add(0, 0, index, selection)
        }
    }
        .also {
            it.setOnMenuItemClickListener { item ->
                this.text = item.title
                return@setOnMenuItemClickListener true
            }
        }
        .show()
}

fun TextView.showSelectionsPopupMenuOnClick(selections: List<String>) {
    this.setOnClickListener {
        this.showSelectionsPopupMenu(selections)
    }
}

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = textPaint.linkColor
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}


fun View.showSelectionsPopupMenu(selections: List<String>, listener: PopupMenu.OnMenuItemClickListener?) {
    PopupMenu(this.context, this).also {
        val menu = it.menu
        selections.forEachIndexed { index, selection ->
            menu.add(0, 0, index, selection)
        }
    }
        .also {
            it.setOnMenuItemClickListener(listener)
        }
        .show()
}


fun View.showSelectionsPopupMenuOnclick(selections: List<String>, listener: PopupMenu.OnMenuItemClickListener?) {
    this.setOnClickListener {
        this.showSelectionsPopupMenu(selections, listener)
    }
}

@SuppressLint("RestrictedApi")
fun View.showSelectionsPopupMenu(selections: ListOrderedMap<String, Int>, listener: PopupMenu.OnMenuItemClickListener?) {
    PopupMenu(this.context, this).also {
        val menu = it.menu
        for (i in 0 until selections.size) {
            menu.add(0, 0, i, selections.get(i))
                .also { item -> item.setIcon(selections.getValue(i)) }
        }
    }
        .also {
            it.setOnMenuItemClickListener(listener)
            MenuPopupHelper(context, it.menu as MenuBuilder, this).also { helper ->
                helper.setForceShowIcon(true)
                helper.show()
            }
        }
}


@SuppressLint("RestrictedApi")
fun View.showSelectionsPopupMenuOnClick(selections: ListOrderedMap<String, Int>, listener: PopupMenu.OnMenuItemClickListener?) {
    this.setOnClickListener {
        this.showSelectionsPopupMenu(selections, listener)
    }
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
            property.setter.call(s.toString()
                .ifBlank { null })
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

fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    this.setTextColor(ContextCompat.getColor(SkeanFrameWork.getContext(), colorRes))
}


fun TextView.addRequireTagAtEnd() {
    val originalText = this.textOrBlank()
    val requireText = "*"
    val builder = SpannableStringBuilder(originalText + requireText)

    builder.setSpan(
        ForegroundColorSpan(Color.RED), originalText.length, builder.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    setText(builder)
}


fun TextView.addRequireTagAtStart() {
    val originalText = this.textOrBlank()
    val requireText = "*"
    val builder = SpannableStringBuilder(requireText + originalText)

    builder.setSpan(
        ForegroundColorSpan(Color.RED), 0, requireText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    setText(builder)
}

fun TextView.toastHintShort() {
    ToastUtils.showShort(this.hint)
}

fun TextView.toastHintLong() {
    ToastUtils.showLong(this.hint)
}

inline fun TextView.doIfBlankAndReturnText(block: (Unit) -> Unit): String {
    val text = textOrBlank()
    if (text.isBlank()) {
        block.invoke(Unit)
    }
    return text
}

fun FrameLayout.setGravity(gravity: Int) {
    (this.layoutParams as FrameLayout.LayoutParams).also { it.gravity = gravity }
        .also { this.layoutParams = it }
}

fun LinearLayout.setGravity(gravity: Int) {
    (this.layoutParams as LinearLayout.LayoutParams).also { it.gravity = gravity }
        .also { this.layoutParams = it }
}

@JvmOverloads
inline fun TabLayout.addOnTabSelectedListener(
    crossinline onTabSelected: (tab: TabLayout.Tab?) -> Unit = { _ -> },
    crossinline onTabUnselected: (tab: TabLayout.Tab?) -> Unit = { _ -> },
    crossinline onTabReselected: (tab: TabLayout.Tab?) -> Unit = { _ -> }
) {
    this.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            onTabSelected.invoke(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            onTabUnselected.invoke(tab)
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            onTabReselected.invoke(tab)
        }

    })
}

@JvmOverloads
inline fun ViewPager.addOnPageChangeListener(
    crossinline onPageScrolled: (position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit = { _, _, _ -> },
    crossinline onPageSelected: (position: Int) -> Unit = { _ -> },
    crossinline onPageScrollStateChanged: (state: Int) -> Unit = { _ -> }
) {
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            onPageScrolled.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            onPageScrollStateChanged.invoke(state)
        }

    })
}

fun SmartRefreshLayout.finishLoad(res: AppResponse<*>) {
    if (res.refresh == true) {
        val noMore = if (!res.success) true else res.noMore == true
        this.finishRefresh(0, res.success, noMore)
    } else if (res.refresh == false) {
        val noMore = if (!res.success) false else res.noMore == true
        this.finishLoadMore(0, res.success, noMore)
    }
}

fun SmartRefreshLayout.postToAutoRefresh(cancelImmediately: Boolean = true) {
    if (state == RefreshState.None) {
        autoRefresh()
    } else {
        val oldListener: OnMultiListener? = ReflectUtils.reflect(this).field("mOnMultiListener").get()
        val wrapper = if (oldListener is OnMultiListenerWrapper) {
            oldListener
        } else {
            OnMultiListenerWrapper(oldListener)
        }
        wrapper.tempListener = object : SimpleMultiListener() {
            override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
                if (newState == RefreshState.None) {
                    autoRefresh()
                    this@postToAutoRefresh.setOnMultiListener(wrapper.originalListener)
                }
            }
        }
        this.setOnMultiListener(wrapper)
        if (cancelImmediately) {
            if (this.state == RefreshState.Refreshing) {
                finishRefresh(0, false, null)
            } else if (this.state == RefreshState.Loading) {
                finishLoadMore(0, false, false)
            }
        }
    }
}

inline fun ImageView.load(
    uri: String?,
    action: RequestBuilder<*>.() -> Unit = {}
) = loadAny(uri, action)

inline fun ImageView.load(
    url: HttpUrl?,
    action: RequestBuilder<*>.() -> Unit = {}
) = loadAny(url, action)

inline fun ImageView.load(
    uri: Uri?,
    action: RequestBuilder<*>.() -> Unit = {}
) = loadAny(uri, action)

inline fun ImageView.load(
    file: File?,
    action: RequestBuilder<*>.() -> Unit = {}
) = loadAny(file, action)

inline fun ImageView.load(
    @DrawableRes drawableResId: Int,
    action: RequestBuilder<*>.() -> Unit = {}
) = loadAny(drawableResId, action)

inline fun ImageView.load(
    drawable: Drawable?,
    action: RequestBuilder<*>.() -> Unit = {}
) = loadAny(drawable, action)

inline fun ImageView.load(
    bitmap: Bitmap?,
    action: RequestBuilder<*>.() -> Unit = {}
) = loadAny(bitmap, action)

@SuppressLint("CheckResult")
inline fun ImageView.loadAny(
    data: Any?,
    action: RequestBuilder<*>.() -> Unit = {}
) {
    Glide.with(this)
        .load(data)
        .apply(action)
        .into(this)
}

inline fun SearchView.setOnQueryTextListener(
    crossinline onQueryTextSubmit: (String?) -> Boolean = { false },
    crossinline onQueryTextChange: (String?) -> Boolean = { false }
) {
    this.setOnQueryTextListener(object : OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?) = onQueryTextSubmit(query)
        override fun onQueryTextChange(newText: String?) = onQueryTextChange(newText)
    })
}


fun SearchView.setOnCloseBtnClickListener(
    listener: View.OnClickListener
) {
    val searchCloseButton = findViewById<View>(R.id.search_close_btn)
    searchCloseButton.setOnClickListener(listener)
}

fun MapView.bindToLifecycle(lifecycle: Lifecycle, savedInstanceSate: Bundle?) {
    this@bindToLifecycle.onCreate(savedInstanceSate)
    lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onResume(owner: LifecycleOwner) {
            this@bindToLifecycle.onResume()
        }

        override fun onPause(owner: LifecycleOwner) {
            this@bindToLifecycle.onPause()
        }


        override fun onDestroy(owner: LifecycleOwner) {
            this@bindToLifecycle.onDestroy()
        }
    })
}


fun BottomNavigationView.setupWithNavController2(navController: NavController, noPopBack: Boolean = true) {
    val navigationBarView = this
    navigationBarView.setOnItemSelectedListener { item ->
        val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
        if (navController.currentDestination!!.parent!!.findNode(item.itemId) is ActivityNavigator.Destination) {
            builder.setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
                .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
                .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
        } else {
            builder.setEnterAnim(androidx.navigation.ui.R.animator.nav_default_enter_anim)
                .setExitAnim(androidx.navigation.ui.R.animator.nav_default_exit_anim)
                .setPopEnterAnim(androidx.navigation.ui.R.animator.nav_default_pop_enter_anim)
                .setPopExitAnim(androidx.navigation.ui.R.animator.nav_default_pop_exit_anim)
        }
        //这里是修改的地方
        if (noPopBack) {
            builder.setPopUpTo(selectedItemId, inclusive = true, saveState = true)
        } else {
            if (item.order and Menu.CATEGORY_SECONDARY == 0) {
                builder.setPopUpTo(
                    navController.graph.findStartDestination().id,
                    inclusive = false,
                    saveState = true
                )
            }
        }
        val options = builder.build()
        return@setOnItemSelectedListener try {
            // TODO provide proper API instead of using Exceptions as Control-Flow.
            navController.navigate(item.itemId, null, options)
            navController.currentDestination?.hierarchy?.any { it.id == item.itemId } == true
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false
        }
    }
    val weakReference = WeakReference(navigationBarView)
    navController.addOnDestinationChangedListener(
        object : NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: android.os.Bundle?
            ) {
                val view = weakReference.get()
                if (view == null) {
                    navController.removeOnDestinationChangedListener(this)
                    return
                }
                if (destination is FloatingWindow) {
                    return
                }
                view.menu.forEach { item ->
                    if (destination.hierarchy.any { it.id == item.itemId }) {
                        item.isChecked = true
                    }
                }
            }
        }
    )
    // Add your own reselected listener
//    navigationBarView.setOnItemReselectedListener { item ->
//        // Pop everything up to the reselected item
//        val reselectedDestinationId = item.itemId
//        navController.popBackStack(reselectedDestinationId, false)
//    }
}



