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

    @BindingAdapter(value = ["refreshListener", "loadMoreListener"], requireAll = false)
    @JvmStatic
    fun SmartRefreshLayout.setRefreshListener(
        refreshListener: OnRefreshListener? = null,
        loadMoreListener: OnLoadMoreListener? = null
    ) {
        refreshListener?.let { this.setOnRefreshListener(refreshListener) }
        loadMoreListener?.let { this.setOnLoadMoreListener(loadMoreListener) }
    }


    @BindingAdapter("bindRefreshFinishEvent")
    @JvmStatic
    fun SmartRefreshLayout.setRefreshFinishEvent(status: RefreshFinishEvent?) {
        if (status != null) {
            if (isRefreshing) {
                this.finishRefresh(0, status.success, status.noMore)
            } else if (isLoading) {
                this.finishLoadMore(0, status.success, status.noMore)
            }
        }
    }
}

