package me.skean.skeanframework.model

/**
 * 一般列表的刷新状态
 */
data class RefreshFinishEvent(val isRefresh: Boolean, val success: Boolean, var noMore: Boolean) {

}