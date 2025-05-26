package me.skean.framework.example.repository

import me.skean.framework.example.net.ArticleApi
import me.skean.framework.example.net.DouBanApi
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.skeanframework.utils.NetworkUtil
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject

/**
 * Created by Skean on 2022/4/21.
 */
 class ArticleRepository()  {

    private val api  = NetworkUtil.createService<ArticleApi>()


    fun getArticle() = api.listArticle()
}