package me.skean.framework.example.model.util
import me.skean.framework.example.model.util.Status.SUCCESS
import me.skean.framework.example.model.util.Status.FAIL
import me.skean.framework.example.model.util.Status.LOADING


data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Resource<T> = Resource(status = SUCCESS, data = data, message = null)

        fun <T> fail(data: T?, message: String): Resource<T> =
            Resource(status = FAIL, data = data, message = message)

        fun <T> loading(data: T?): Resource<T> = Resource(status = LOADING, data = data, message = null)
    }
}