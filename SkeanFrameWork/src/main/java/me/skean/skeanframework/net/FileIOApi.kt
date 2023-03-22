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
    val baseUrl
        get() = "http://useless.com/"

    @GET
    @Streaming
    suspend fun download(@Url url: String?): ResponseBody?

    @GET
    @Streaming
    fun downloadWithCall(@Url url: String?): Call<ResponseBody?>

    @GET
    @Streaming
    fun downloadWithSingle(@Url url: String?): Single<ResponseBody?>

    @GET
    @Streaming
    fun downloadWithObservable(@Url url: String?): Observable<ResponseBody?>


    @POST
    @Multipart
    @Streaming
    fun upload(@Url url: String?, @Part file: MultipartBody.Part?): ResponseBody?

    @POST
    @Multipart
    @Streaming
    fun uploadWithCall(@Url url: String?, @Part file: MultipartBody.Part?): Call<ResponseBody?>


    @POST
    @Multipart
    @Streaming
    fun uploadWithSingle(@Url url: String?, @Part file: MultipartBody.Part?): Single<ResponseBody?>


    @POST
    @Multipart
    @Streaming
    fun uploadWithObservable(@Url url: String?, @Part file: MultipartBody.Part?): Observable<ResponseBody?>

}