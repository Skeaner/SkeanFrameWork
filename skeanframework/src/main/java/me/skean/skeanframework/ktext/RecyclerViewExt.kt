package me.skean.skeanframework.ktext

import android.content.Context
import android.text.Editable
import android.view.View
import android.widget.Checkable
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import me.skean.skeanframework.R
import me.skean.skeanframework.delegate.UnionClickListener

/**
 * Created by Skean on 21/6/1.
 */
fun BaseQuickAdapter<*, *>.setUnionClickListener(listener: UnionClickListener) {
    this.setOnItemClickListener(listener)
    this.setOnItemLongClickListener(listener)
    this.setOnItemChildClickListener(listener)
    this.setOnItemChildLongClickListener(listener)
}

fun GridLayoutManager.setSpanSizeLookup(spanSizeLookupAction: (position: Int) -> Int): GridLayoutManager {
    this.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return spanSizeLookupAction.invoke(position)
        }
    }
    return this
}

fun BaseViewHolder.setChecked(@IdRes viewId: Int, isChecked: Boolean): BaseViewHolder {
    (this.getView<View>(viewId) as? Checkable)?.isChecked = isChecked
    return this
}


inline fun BaseViewHolder.addTextChangedListener(
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
): BaseViewHolder {
    this.getView<EditText>(viewId).addTextChangedListener(beforeTextChanged, onTextChanged, afterTextChanged)
    return this
}

@JvmOverloads
fun horizontalDividerItemDecoration(
    context: Context,
    @ColorInt color: Int = ContextCompat.getColor(context, R.color.dividerGray),
    sizeInDp: Float = 1f,
    action: FlexibleDividerDecoration.Builder<*>.() -> Unit = {}
): RecyclerView.ItemDecoration {
    return HorizontalDividerItemDecoration.Builder(context)
        .color(color)
        .size(sizeInDp.dp2px())
        .apply(action)
        .build()
}

@JvmOverloads
fun verticalDividerItemDecoration(
    context: Context,
    @ColorInt color: Int = ContextCompat.getColor(context, R.color.dividerGray),
    sizeInDp: Float = 1f,
    action: FlexibleDividerDecoration.Builder<*>.() -> Unit = {}
): RecyclerView.ItemDecoration {
    return VerticalDividerItemDecoration.Builder(context)
        .color(color)
        .apply(action)
        .size(sizeInDp.dp2px())
        .build()
}

@JvmOverloads
fun RecyclerView.addDividerItemDecoration(
    @ColorInt color: Int = ContextCompat.getColor(context, R.color.dividerGray),
    sizeInDp: Float = 1f,
    action: FlexibleDividerDecoration.Builder<*>.() -> Unit = {}
) {
    if (layoutManager is LinearLayoutManager) {
        if ((layoutManager as LinearLayoutManager).orientation == RecyclerView.VERTICAL) {
            this.addItemDecoration(horizontalDividerItemDecoration(context, color, sizeInDp, action))
        }
        if ((layoutManager as LinearLayoutManager).orientation == RecyclerView.HORIZONTAL) {
            this.addItemDecoration(verticalDividerItemDecoration(context, color, sizeInDp, action))
        }
    }
}