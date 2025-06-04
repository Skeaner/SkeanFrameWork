package me.skean.skeanframework.model

/**
 * 一般列表的刷新状态
 */
data class RefreshFinishEvent( val success: Boolean, var noMore: Boolean) {

}