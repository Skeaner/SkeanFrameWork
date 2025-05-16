package me.skean.framework.example.net

import io.reactivex.rxjava3.core.Single
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.skeanframework.net.BaseUrl
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Skean on 2022/4/21.
 */
interface DouBanApi {
    companion object {
        @BaseUrl
        @JvmField
        val baseUrl = "https://api.wmdb.tv/"
    }


    @GET("api/v1/top")
    fun listMovie(
        @Query("type") type: String? = "Imdb", @Query("lang") lang: String? = "Cn",
        @Query("skip") skip: Int?,
        @Query("limit") limit: Int
    ): Single<List<MovieInfo>>
}