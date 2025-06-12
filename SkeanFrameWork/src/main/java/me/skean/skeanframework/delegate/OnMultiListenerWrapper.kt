package me.skean.skeanframework.delegate

import android.annotation.SuppressLint
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnMultiListener

/**
 * Created by Skean on 2025/06/11.
 */
class OnMultiListenerWrapper(val originalListener: OnMultiListener?) : OnMultiListener {

    var tempListener: OnMultiListener? = null


    override fun onRefresh(refreshLayout: RefreshLayout) {
        originalListener?.onRefresh(refreshLayout)
        tempListener?.onRefresh(refreshLayout)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        originalListener?.onLoadMore(refreshLayout)
        tempListener?.onLoadMore(refreshLayout)
    }

    @SuppressLint("RestrictedApi")
    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        originalListener?.onStateChanged(refreshLayout, oldState, newState)
        tempListener?.onStateChanged(refreshLayout, oldState, newState)
    }

    override fun onHeaderMoving(
        header: RefreshHeader?,
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        headerHeight: Int,
        maxDragHeight: Int
    ) {
        originalListener?.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight)
        tempListener?.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight)
    }

    override fun onHeaderReleased(header: RefreshHeader?, headerHeight: Int, maxDragHeight: Int) {
        originalListener?.onHeaderReleased(header, headerHeight, maxDragHeight)
        tempListener?.onHeaderReleased(header, headerHeight, maxDragHeight)
    }

    override fun onHeaderStartAnimator(header: RefreshHeader?, headerHeight: Int, maxDragHeight: Int) {
        originalListener?.onHeaderStartAnimator(header, headerHeight, maxDragHeight)
        tempListener?.onHeaderStartAnimator(header, headerHeight, maxDragHeight)
    }

    override fun onHeaderFinish(header: RefreshHeader?, success: Boolean) {
        originalListener?.onHeaderFinish(header, success)
        tempListener?.onHeaderFinish(header, success)
    }

    override fun onFooterMoving(
        footer: RefreshFooter?,
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        footerHeight: Int,
        maxDragHeight: Int
    ) {
        originalListener?.onFooterMoving(footer, isDragging, percent, offset, footerHeight, maxDragHeight)
        tempListener?.onFooterMoving(footer, isDragging, percent, offset, footerHeight, maxDragHeight)
    }

    override fun onFooterReleased(footer: RefreshFooter?, footerHeight: Int, maxDragHeight: Int) {
        originalListener?.onFooterReleased(footer, footerHeight, maxDragHeight)
        tempListener?.onFooterReleased(footer, footerHeight, maxDragHeight)
    }

    override fun onFooterStartAnimator(footer: RefreshFooter?, footerHeight: Int, maxDragHeight: Int) {
        originalListener?.onFooterStartAnimator(footer, footerHeight, maxDragHeight)
        tempListener?.onFooterStartAnimator(footer, footerHeight, maxDragHeight)
    }

    override fun onFooterFinish(footer: RefreshFooter?, success: Boolean) {
        originalListener?.onFooterFinish(footer, success)
        tempListener?.onFooterFinish(footer, success)
    }
}