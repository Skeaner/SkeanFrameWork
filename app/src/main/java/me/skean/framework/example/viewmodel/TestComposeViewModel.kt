package me.skean.framework.example.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.king.ultraswiperefresh.UltraSwipeRefreshState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.framework.example.repository.DouBanRepository
import me.skean.skeanframework.component.BaseVm
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Created by Skean on 2025/06/11.
 */
class TestComposeViewModel : BaseVm(), KoinComponent {
    private val repository = get<DouBanRepository>()

    val refreshState = UltraSwipeRefreshState(isRefreshing = false, isLoading = false)
    var isNoMoreState = mutableStateOf(false)
    val data = mutableStateListOf<MovieInfo.Data>()
    private var currentPage = 0

    private fun requestData(refresh: Boolean) {
        if (refresh) currentPage = 0
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            ToastUtils.showShort(e.localizedMessage)
            if (refresh) {
                refreshState.isRefreshing = false
                isNoMoreState.value = false
            } else {
                refreshState.isLoading = false
                isNoMoreState.value = true
            }
        }) {
            val list = repository.listMovie(currentPage).map { it.data?.firstOrNull()!! }
            currentPage++
            val noMore = list.size < 10
            if (refresh) {
                refreshState.isRefreshing = false
            } else {
                refreshState.isLoading = false
            }
            isNoMoreState.value = noMore
        }
    }
}