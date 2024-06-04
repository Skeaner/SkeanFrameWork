package me.skean.framework.example.viewmodel

import android.app.Application
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import me.goldze.mvvmhabit.base.BaseViewModel
import me.goldze.mvvmhabit.binding.command.BindingAction
import me.goldze.mvvmhabit.binding.command.BindingCommand
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent
import me.goldze.mvvmhabit.utils.RxUtils
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.framework.example.repository.DouBanRepository
import me.skean.skeanframework.ktext.bindToVmLifecycle
import me.skean.skeanframework.ktext.defaultSingleObserver
import me.skean.skeanframework.ktext.subscribeOnIoObserveOnMainThread
import org.koin.core.component.KoinComponent


/**
 * Created by Skean on 2022/4/20.
 */
class TestMvvmViewModel(app: Application) : BaseViewModel<DouBanRepository>(app), KoinComponent {
    class UIChangeObservable {
        val finishRefreshing: SingleLiveEvent<Pair<Boolean, Boolean>> = SingleLiveEvent()
        val finishLoadMore: SingleLiveEvent<Pair<Boolean, Boolean>> = SingleLiveEvent()
    }

    val uc: UIChangeObservable = UIChangeObservable()
    val data = SingleLiveEvent<List<MovieInfo.Data>>()

    private var movieList: MutableList<MovieInfo.Data> = mutableListOf()
    private var currentPage = 0

    val refreshListener = OnRefreshListener {
        requestData(true)
    }

    val loadMoreListener = OnLoadMoreListener {
        requestData(false)
    }

    private fun requestData(refresh: Boolean) {
        if (refresh) currentPage = 0
        DouBanRepository.listMovie(currentPage)
            .subscribeOnIoObserveOnMainThread()
            .bindToVmLifecycle(lifecycleProvider)
            .subscribe(defaultSingleObserver(onError2 = {
                if (refresh) {
                    uc.finishRefreshing.value = false to true
                } else {
                    uc.finishLoadMore.value = false to true
                }
            }) {
                val list = it.map { it.data?.firstOrNull()!! }
                currentPage++
                val noMore = list.size < 10
                if (refresh) {
                    uc.finishRefreshing.value = true to noMore
                    movieList = list.toMutableList()
                } else {
                    uc.finishLoadMore.value = true to noMore
                    movieList.addAll(list)
                }
                data.value = movieList
            })
    }
}

