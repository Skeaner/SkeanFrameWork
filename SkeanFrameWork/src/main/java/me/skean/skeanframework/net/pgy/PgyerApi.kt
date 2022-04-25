package me.skean.skeanframework.net.pgy

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * 蒲公英托管的服务
 */
interface PgyerApi  {

     var baseUrl: String
        get() = "http://www.pgyer.com/apiv1/app/"
        set(value) {}

    @FormUrlEncoded
    @POST("viewGroup")
    fun getAppInfo(@Field("aId") appId: String?, @Field("_api_key") apiKey: String?): Observable<PgyAppInfo?>

    @GET("install")
    fun downLoadApk(@Query("aId") appId: String?, @Query("_api_key") apiKey: String?): Call<ResponseBody?>

    @Multipart
    @POST("upload")
    fun uploadApk(@Part("uKey") userkey: RequestBody?,
                  @Part("_api_key") apiKey: RequestBody?,
                  @Part file: MultipartBody.Part?): Call<ResponseBody?>

}