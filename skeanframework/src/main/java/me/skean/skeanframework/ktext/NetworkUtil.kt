package me.skean.skeanframework.ktext

import me.skean.skeanframework.net.BaseNetService
import me.skean.skeanframework.utils.NetworkUtil
import okhttp3.Interceptor

/**
 * Created by Skean on 20/9/1.
 */
object NetworkUtil {

    inline fun <reified T : Any> buildService(): T {
        return NetworkUtil.buildService<T>(T::class.java)
    }

    inline fun <reified T : BaseNetService> buildService(vararg interceptors: Interceptor): T {
        return NetworkUtil.buildService<T>(T::class.java, *interceptors)
    }
}