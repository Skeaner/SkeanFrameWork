package me.skean.skeanframework.delegate

import android.view.View
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener

class AdvanceOnItemChildClickListener : OnItemChildClickListener {

    private val delegateMaps = hashMapOf<Int, (BaseQuickAdapter<*, *>, View, Int) -> Unit>()

    fun addDelegate(viewId: Int, delegate: (BaseQuickAdapter<*, *>, View, Int) -> Unit) {
        delegateMaps[viewId] = delegate
    }

    fun removeDelegate(viewId: Int) {
        delegateMaps.remove(viewId)
    }

    fun isDelegateMapsEmpty(): Boolean {
        return delegateMaps.isEmpty()
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val viewId = view.id
        if (delegateMaps.containsKey(viewId)) {
            delegateMaps[viewId]?.invoke(adapter, view, position)
        }
    }
}