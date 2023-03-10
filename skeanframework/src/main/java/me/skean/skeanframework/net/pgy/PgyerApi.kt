package me.skean.skeanframework.net.pgy

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * 蒲公英托管的服务
 */
interface PgyerApi  {

    @JvmDefault
     val baseUrl: String
        get() = "http://www.pgyer.com/apiv2/app/"



    @POST("view")
    @FormUrlEncoded
    fun getAppInfo(
        @Field("appKey") appKey: String,
        @Field("_api_key") apiKey: String,
    ): Single<PgyerResult<PgyAppDetail>>

    @POST("check")
    @FormUrlEncoded
    fun checkUpdate(
        @Field("appKey") appKey: String,
        @Field("_api_key") apiKey: String,
        @Field("buildVersion") versionName: String,
        @Field("buildBuildVersion") versionCode: Int,
    ): Single<PgyerResult<PgyerAppInfo>>
}