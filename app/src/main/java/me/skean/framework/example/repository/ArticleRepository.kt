package me.skean.framework.example.repository

import io.reactivex.Single
import me.skean.framework.example.net.ArticleApi
import me.skean.framework.example.net.DouBanApi
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.skeanframework.utils.NetworkUtil
import org.koin.androidx.compose.inject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Created by Skean on 2022/4/21.
 */
object ArticleRepository : KoinComponent {
    private val api by inject<ArticleApi>()


    fun getArticle() = api.listArticle()
}