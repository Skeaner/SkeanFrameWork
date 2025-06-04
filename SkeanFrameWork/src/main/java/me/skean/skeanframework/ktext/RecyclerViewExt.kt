package me.skean.skeanframework.ktext

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.view.View
import android.widget.Checkable
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import me.skean.skeanframework.R
import java.util.Objects
import kotlin.reflect.KProperty1

/**
 * Created by Skean on 21/6/1.
 */


fun GridLayoutManager.setSpanSizeLookup(spanSizeLookupAction: (position: Int) -> Int): GridLayoutManager {
    this.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return spanSizeLookupAction.invoke(position)
        }
    }
    return this
}

fun QuickViewHolder.setChecked(@IdRes viewId: Int, isChecked: Boolean): QuickViewHolder {
    (this.getView<View>(viewId) as? Checkable)?.isChecked = isChecked
    return this
}


inline fun QuickViewHolder.addTextChangedListener(
    @IdRes viewId: Int,
    crossinline beforeTextChanged: (
        text: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) -> Unit = { _, _, _, _ -> },
    crossinline onTextChanged: (
        text: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) -> Unit = { _, _, _, _ -> },
    crossinline afterTextChanged: (text: Editable?) -> Unit = {}
): QuickViewHolder {
    this.getView<EditText>(viewId).addTextChangedListener(beforeTextChanged, onTextChanged, afterTextChanged)
    return this
}

@JvmOverloads
fun horizontalDividerItemDecoration(
    context: Context,
    @ColorInt color: Int = ContextCompat.getColor(context, R.color.dividerGray),
    sizeInDp: Float = 1f,
    showAtLast: Boolean = false,
    action: FlexibleDividerDecoration.Builder<*>.() -> Unit = {}
): RecyclerView.ItemDecoration {
    return HorizontalDividerItemDecoration.Builder(context)
        .color(color)
        .size(sizeInDp.dp2px())
        .apply {
            if (showAtLast) showLastDivider()
            action.invoke(this)
        }
        .build()
}

@JvmOverloads
fun verticalDividerItemDecoration(
    context: Context,
    @ColorInt color: Int = ContextCompat.getColor(context, R.color.dividerGray),
    sizeInDp: Float = 1f,
    showAtLast: Boolean = false,
    action: FlexibleDividerDecoration.Builder<*>.() -> Unit = {}
): RecyclerView.ItemDecoration {
    return VerticalDividerItemDecoration.Builder(context)
        .color(color)
        .apply {
            if (showAtLast) showLastDivider()
            action.invoke(this)
        }
        .size(sizeInDp.dp2px())
        .build()
}

@JvmOverloads
fun RecyclerView.addDividerItemDecoration(
    @ColorInt color: Int = ContextCompat.getColor(context, R.color.dividerGray),
    sizeInDp: Float = 1f,
    showAtLast: Boolean = false,
    action: FlexibleDividerDecoration.Builder<*>.() -> Unit = {}
) {
    if (layoutManager is LinearLayoutManager) {
        if ((layoutManager as LinearLayoutManager).orientation == RecyclerView.VERTICAL) {
            this.addItemDecoration(horizontalDividerItemDecoration(context, color, sizeInDp, showAtLast, action))
        }
        if ((layoutManager as LinearLayoutManager).orientation == RecyclerView.HORIZONTAL) {
            this.addItemDecoration(verticalDividerItemDecoration(context, color, sizeInDp, showAtLast, action))
        }
    }
}

@JvmOverloads
fun RecyclerView.addMarginItemDecoration(
    @ColorInt color: Int = Color.TRANSPARENT,
    sizeInDp: Float = 5f,
    showAtLast: Boolean = true,
    action: FlexibleDividerDecoration.Builder<*>.() -> Unit = {}
) {
    if (layoutManager is LinearLayoutManager) {
        if ((layoutManager as LinearLayoutManager).orientation == RecyclerView.VERTICAL) {
            this.addItemDecoration(horizontalDividerItemDecoration(context, color, sizeInDp, showAtLast, action))
        }
        if ((layoutManager as LinearLayoutManager).orientation == RecyclerView.HORIZONTAL) {
            this.addItemDecoration(verticalDividerItemDecoration(context, color, sizeInDp, showAtLast, action))
        }
    }
}



fun <T : Any> quickDiffCallback(
    isSameComparator: (T, T) -> Boolean = { oldItem, newItem -> oldItem == newItem },
    contentEqualComparator: (T, T) -> Boolean = { oldItem, newItem -> oldItem == newItem }
): DiffUtil.ItemCallback<T> {
    return object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = isSameComparator(oldItem, newItem)

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = contentEqualComparator(oldItem, newItem)
    }
}

fun <T : Any> propsComparator(vararg compareProps: KProperty1<T, Any?>): (T, T) -> Boolean {
    return object : (T, T) -> Boolean {
        override fun invoke(oldItem: T, newItem: T): Boolean {
            if (oldItem == newItem) return true
            for (prop in compareProps) {
                if (!Objects.equals(prop.get(oldItem), prop.get(newItem))) {
                    return false
                }
            }
            return true
        }
    }
}
