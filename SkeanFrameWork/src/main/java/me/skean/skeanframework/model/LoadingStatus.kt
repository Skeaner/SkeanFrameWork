package me.skean.skeanframework.model

/**
 * Created by Skean on 2022/9/15.
 */
data class LoadingStatus(val status: Status, var cancelable: Boolean, var tips: String?) {
    companion object {
        fun loading(cancelable: Boolean = true, tips: String? = null): LoadingStatus = LoadingStatus(Status.Loading, cancelable, tips)
        fun success(cancelable: Boolean = true, tips: String? = null): LoadingStatus = LoadingStatus(Status.Success, cancelable, tips)
        fun fail(cancelable: Boolean = true, tips: String? = null): LoadingStatus = LoadingStatus(Status.Fail, cancelable, tips)
    }

    enum class Status {
        Loading, Success, Fail
    }

}