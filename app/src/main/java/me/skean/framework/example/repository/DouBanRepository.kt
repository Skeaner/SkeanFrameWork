package me.skean.framework.example.repository

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Single
import me.skean.framework.example.net.DouBanApi
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.skeanframework.utils.NetworkUtil
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Created by Skean on 2022/4/21.
 */
object DouBanRepository : ViewModel(),KoinComponent {
    private val douBanApi by inject<DouBanApi>()


    fun listMovie(page: Int): Single<List<MovieInfo>> {
        return douBanApi.listMovie(skip = page * 10, limit = 10)
    }
}