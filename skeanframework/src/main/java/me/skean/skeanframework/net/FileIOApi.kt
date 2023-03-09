package me.skean.skeanframework.net

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * 一些文件IO服务
 */
interface FileIOApi {

    @JvmDefault
    val baseUrl get() = "http://useless.com/"

    @GET
    fun downLoad(@Url url: String?): Call<ResponseBody?>

    @GET
    fun downLoadSingle(@Url url: String?): Single<ResponseBody?>

    @POST
    @Multipart
    @Streaming
    fun upload(@Url url: String?, @Part file: MultipartBody.Part?): Call<ResponseBody?>


    @POST
    @Multipart
    @Streaming
    fun uploadSingle(@Url url: String?, @Part file: MultipartBody.Part?): Single<ResponseBody?>

}