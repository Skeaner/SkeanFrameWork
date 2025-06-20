package me.skean.framework.example.viewmodel

import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import me.hgj.jetpackmvvm.callback.livedata.event.EventLiveData
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.framework.example.repository.DouBanRepository
import me.skean.skeanframework.component.BaseVm
import me.skean.skeanframework.ktext.toMutableListAddAll
import me.skean.skeanframework.model.RefreshFinishEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


/**
 * Created by Skean on 2022/4/20.
 */
class TestMvvmViewModel() : BaseVm(), KoinComponent {

    private val repository = get<DouBanRepository>()

    val refreshCompleteEvent: EventLiveData<RefreshFinishEvent> = EventLiveData()

    val data = EventLiveData<List<MovieInfo.Data>>()

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
            refreshCompleteEvent.value = RefreshFinishEvent(success = false, noMore = true)
        }) {
            val list = repository.listMovie(currentPage).map { it.data?.firstOrNull()!! }
            currentPage++
            val noMore = list.size < 10
            refreshCompleteEvent.value = RefreshFinishEvent(success = true, noMore = noMore)
            movieList = if (refresh) list.toMutableList() else movieList.toMutableListAddAll(list)
            data.value = movieList
        }
    }
}

