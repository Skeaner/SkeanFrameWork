package me.skean.skeanframework.net.pgy

import io.reactivex.rxjava3.core.Single
import me.skean.skeanframework.net.BaseUrl
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * 蒲公英托管的服务
 */
interface PgyerApi {

    companion object {
        @JvmField
        @BaseUrl
        val baseUrl: String = "https://www.pgyer.com/apiv2/app/"
    }


    @POST("view")
    @FormUrlEncoded
    fun getAppInfo(
        @Field("appKey") appKey: String,
        @Field("_api_key") apiKey: String,
    ): Single<PgyerResult<PgyAppDetail>>

    @POST("view")
    @FormUrlEncoded
    suspend fun getAppInfo2(
        @Field("appKey") appKey: String,
        @Field("_api_key") apiKey: String,
    ): PgyerResult<PgyAppDetail>

    @POST("check")
    @FormUrlEncoded
    fun checkUpdate(
        @Field("appKey") appKey: String,
        @Field("_api_key") apiKey: String,
        @Field("buildVersion") versionName: String,
        @Field("buildBuildVersion") versionCode: Int,
    ): Single<PgyerResult<PgyerAppInfo>>

    @POST("check")
    @FormUrlEncoded
    suspend fun checkUpdate2(
        @Field("appKey") appKey: String,
        @Field("_api_key") apiKey: String,
        @Field("buildVersion") versionName: String,
        @Field("buildBuildVersion") versionCode: Int,
    ): PgyerResult<PgyerAppInfo>

}