package me.skean.framework.example.net

import io.reactivex.Single
import me.skean.framework.example.net.bean.ArticleResponse
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.skeanframework.net.BaseUrl
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Skean on 2022/4/21.
 */
interface ArticleApi {
    companion object {
        @BaseUrl
        @JvmField
        val baseUrl = "https://www.wanandroid.com/"
    }


    @GET("article/list/0/json")
    fun listArticle(): Single<ArticleResponse>
}