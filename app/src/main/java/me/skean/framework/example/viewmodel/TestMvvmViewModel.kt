package me.skean.framework.example.viewmodel

import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.framework.example.repository.DouBanRepository
import me.skean.skeanframework.component.BaseVm
import me.skean.skeanframework.model.RefreshFinishEvent
import me.skean.skeanframework.utils.SingleLiveEvent
import org.koin.androidx.compose.get
import org.koin.compose.currentKoinScope
import org.koin.compose.koinInject
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


/**
 * Created by Skean on 2022/4/20.
 */
class TestMvvmViewModel() : BaseVm(), KoinComponent {

    private val repository = get<DouBanRepository>()

    val refreshCompleteEvent: SingleLiveEvent<RefreshFinishEvent> = SingleLiveEvent()

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
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            ToastUtils.showShort(e.localizedMessage)
            refreshCompleteEvent.value = RefreshFinishEvent(isRefresh = refresh, success = false, noMore = true)
        }) {
            val list = repository.listMovie(currentPage).map { it.data?.firstOrNull()!! }
            currentPage++
            val noMore = list.size < 10
            refreshCompleteEvent.value = RefreshFinishEvent(isRefresh = refresh, success = true, noMore = noMore)
            if (refresh) movieList = list.toMutableList() else movieList.addAll(list)
            data.value = movieList
        }
    }
}

