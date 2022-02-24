package me.skean.skeanframework.net

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * 一些文件IO服务
 */
interface FileIOApi {

    var baseUrl: String
        get() = "http://useless.com/"
        set(value) {}

    @GET
    fun downLoad(@Url url: String?): Call<ResponseBody?>

    @POST
    @Multipart
    @Streaming
    fun upload(@Url url: String?, @Part file: MultipartBody.Part?): Call<ResponseBody?>

}