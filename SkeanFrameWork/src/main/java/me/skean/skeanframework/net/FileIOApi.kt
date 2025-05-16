package me.skean.skeanframework.net

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * 一些文件IO服务
 */
interface FileIOApi {

    companion object{
        @JvmField
        @BaseUrl
        val baseUrl = "http://useless.com/"
    }

    @GET
    @Streaming
    suspend fun download(@Url url: String?): ResponseBody?

    @GET
    @Streaming
    fun downloadToCall(@Url url: String?): Call<ResponseBody?>

    @GET
    @Streaming
    fun downloadToSingle(@Url url: String?): Single<ResponseBody>

    @GET
    @Streaming
    fun downloadToObservable(@Url url: String?): Observable<ResponseBody>


    @POST
    @Multipart
    @Streaming
    fun upload(@Url url: String?, @Part file: MultipartBody.Part?): ResponseBody?

    @POST
    @Multipart
    @Streaming
    fun uploadToCall(@Url url: String?, @Part file: MultipartBody.Part?): Call<ResponseBody?>


    @POST
    @Multipart
    @Streaming
    fun uploadToSingle(@Url url: String?, @Part file: MultipartBody.Part?): Single<ResponseBody>


    @POST
    @Multipart
    @Streaming
    fun uploadToObservable(@Url url: String?, @Part file: MultipartBody.Part?): Observable<ResponseBody>

}