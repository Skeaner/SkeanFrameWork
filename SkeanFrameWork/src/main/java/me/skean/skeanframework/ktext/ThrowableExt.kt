package me.skean.skeanframework.ktext

import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.JsonObject
import me.skean.skeanframework.utils.NetworkUtil
import org.json.JSONObject
import retrofit2.HttpException
import kotlin.reflect.KProperty1

/**
 * Created by Skean on 21/12/1.
 */
fun Throwable.parseHttpErrorBody(fieldName: String): String {
    try {
        if (this is HttpException) {
            val code = this.code()
            val errorBody = this.response()?.errorBody()?.string() ?: "{}"
            val jo = JSONObject(errorBody)
            return "$code: ${jo.getString(fieldName)}"
        } else {
            return localizedMessage
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return "未知错误"
}


inline fun <reified T> Throwable.parseHttpErrorBody(property: KProperty1<T, String?>): String {
    try {
        if (this is HttpException) {
            val code = this.code()
            val errorBody = this.response()?.errorBody()?.string() ?: "{}"
            val jo = JSONObject(errorBody)
            return "$code: ${jo.getString(property.name)}"
        } else {
            return localizedMessage
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return "未知错误"
}