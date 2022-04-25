package me.skean.skeanframework.ktext

import okhttp3.Request
import okio.Buffer
import java.io.IOException

/**
 * Created by Skean on 21/4/28.
 */
fun Request.bodyToString():String{
    return try {
        val copy: Request = newBuilder().build()
        val buffer = Buffer()
        copy.body!!.writeTo(buffer)
        buffer.readUtf8()
    } catch (e: IOException) {
        "Request Parse Body To String Fail"
    }
}