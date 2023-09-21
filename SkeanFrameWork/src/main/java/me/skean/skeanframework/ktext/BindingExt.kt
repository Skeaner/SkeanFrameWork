@file:JvmName("BindingExt")

package me.skean.skeanframework.ktext

import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethods
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

/**
 * Created by Skean on 2023/9/21.
 */
@BindingMethods(
    value = [
    ]
)
object BindingExt {

    @BindingAdapter("onRefreshListener")
    @JvmStatic
    fun SmartRefreshLayout.bindOnRefreshListener(refreshListener: OnRefreshListener) {
        setOnRefreshListener(refreshListener)
    }

    @BindingAdapter("onLoadMoreListener")
    @JvmStatic
    fun SmartRefreshLayout.bindOnLoadMoreListener(loadMoreListener: OnLoadMoreListener) {
        setOnLoadMoreListener(loadMoreListener)
    }
}

