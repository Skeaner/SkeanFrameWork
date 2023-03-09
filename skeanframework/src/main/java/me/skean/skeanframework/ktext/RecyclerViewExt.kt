package me.skean.skeanframework.ktext

import android.text.Editable
import android.view.View
import android.widget.Checkable
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.skean.skeanframework.delegate.AdvanceOnItemChildClickListener
import me.skean.skeanframework.delegate.AdvanceOnItemChildLongClickListener
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

fun BaseQuickAdapter<*, *>.addOnItemChildClickListener(viewId: Int, delegate: (BaseQuickAdapter<*, *>, View, Int) -> Unit) {
    var itemChildClickListener = this.getOnItemChildClickListener()
    if (itemChildClickListener == null || itemChildClickListener !is AdvanceOnItemChildClickListener) {
        itemChildClickListener = AdvanceOnItemChildClickListener()
        this.setOnItemChildClickListener(itemChildClickListener)
    }
    addChildClickViewIds(viewId)
    itemChildClickListener.addDelegate(viewId, delegate)
}

fun BaseQuickAdapter<*, *>.removeOnItemChildClickListener(viewId: Int) {
    val itemChildClickListener = this.getOnItemChildClickListener()
    getChildClickViewIds().remove(viewId)
    if (itemChildClickListener is AdvanceOnItemChildClickListener) {
        itemChildClickListener.removeDelegate(viewId)
        if (itemChildClickListener.isDelegateMapsEmpty()) {
            setOnItemChildClickListener(null)
        }
    }
}



fun BaseQuickAdapter<*, *>.addOnItemChildLongClickListener(viewId: Int, delegate: (BaseQuickAdapter<*, *>, View, Int) -> Unit) {
    var itemChildLongClickListener = this.getOnItemChildLongClickListener()
    if (itemChildLongClickListener == null || itemChildLongClickListener !is AdvanceOnItemChildLongClickListener) {
        itemChildLongClickListener = AdvanceOnItemChildLongClickListener()
        setOnItemChildLongClickListener(itemChildLongClickListener)
    }
    addChildLongClickViewIds(viewId)
    itemChildLongClickListener.addDelegate(viewId, delegate)
}

fun BaseQuickAdapter<*, *>.removeOnItemChildLongClickListener(viewId: Int) {
    val itemChildLongClickListener = getOnItemChildLongClickListener()
    getChildLongClickViewIds().remove(viewId)
    if (itemChildLongClickListener is AdvanceOnItemChildLongClickListener) {
        itemChildLongClickListener.removeDelegate(viewId)
        if (itemChildLongClickListener.isDelegateMapsEmpty()) {
            setOnItemChildLongClickListener(null)
        }
    }
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