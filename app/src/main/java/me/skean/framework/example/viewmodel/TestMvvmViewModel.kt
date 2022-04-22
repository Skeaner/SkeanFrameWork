package me.skean.framework.example.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.Single
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.skeanframework.model.AppResponse
import me.skean.framework.example.repository.DouBanRepository
import me.skean.skeanframework.ktext.subscribeOnIoObserveOnMainThread
import org.koin.core.component.KoinComponent

/**
 * Created by Skean on 2022/4/20.
 */
class TestMvvmViewModel : ViewModel(), KoinComponent {
    private var currentPage = 0

    fun requestData(refresh: Boolean): Single<AppResponse<MutableList<MovieInfo>>> {
        if (refresh) currentPage = 0
        return DouBanRepository
            .listMovie(currentPage)
            .subscribeOnIoObserveOnMainThread()
            .map {
                currentPage++
                AppResponse(success = true, refresh = refresh, noMore = currentPage > 20, result = it.toMutableList())
            }
            .onErrorResumeNext {
                Single.just(AppResponse(success = false, refresh = refresh, msg = it.localizedMessage, result = mutableListOf()))
            }
    }


}

