package me.skean.framework.example.repository

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Single
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
class DouBanRepository(val api:DouBanApi ) {

    suspend fun listMovie(page: Int): List<MovieInfo> {
        return api.listMovie(skip = page * 10, limit = 10)
    }
}