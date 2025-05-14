package me.skean.framework.example.repository

import io.reactivex.Single
import me.goldze.mvvmhabit.base.BaseModel
import me.skean.framework.example.net.DouBanApi
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.skeanframework.utils.NetworkUtil
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Created by Skean on 2022/4/21.
 */
object DouBanRepository : BaseModel(),KoinComponent {
    private val douBanApi by inject<DouBanApi>()


    fun listMovie(page: Int): Single<List<MovieInfo>> {
        return douBanApi.listMovie(skip = page * 10, limit = 10)
    }
}