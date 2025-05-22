@file:JvmName("BindingExt")

package me.skean.skeanframework.bindingext

import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethods
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import me.skean.skeanframework.model.RefreshFinishEvent

/**
 * Created by Skean on 2023/9/21.
 */
@BindingMethods(
    value = [
    ]
)
object SmartRefreshLayoutDbExt {

    @BindingAdapter("bindRefreshListener")
    @JvmStatic
    fun SmartRefreshLayout.bindRefreshListener(refreshListener: OnRefreshListener) {
        setOnRefreshListener(refreshListener)
    }

    @BindingAdapter("bindLoadMoreListener")
    @JvmStatic
    fun SmartRefreshLayout.bindLoadMoreListener(loadMoreListener: OnLoadMoreListener) {
        setOnLoadMoreListener(loadMoreListener)
    }

    @BindingAdapter("bindRefreshFinishEvent")
    @JvmStatic
    fun SmartRefreshLayout.bindRefreshFinishEvent(status: RefreshFinishEvent?) {
        if (status != null) {
            if (status.isRefresh) {
                this.finishRefresh(0, status.success, status.noMore)
            } else {
                this.finishLoadMore(0, status.success, status.noMore)
            }
        }
    }
}

