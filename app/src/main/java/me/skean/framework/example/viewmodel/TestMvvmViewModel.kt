package me.skean.framework.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.blankj.utilcode.util.FileIOUtils
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import me.skean.framework.example.component.App
import me.skean.framework.example.model.util.Resource
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.skeanframework.model.AppResponse
import me.skean.framework.example.repository.DouBanRepository
import me.skean.skeanframework.ktext.subscribeOnIoObserveOnMainThread
import me.skean.skeanframework.net.FileIOApi
import me.skean.skeanframework.utils.NetworkUtil
import org.koin.core.component.KoinComponent
import java.io.File

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

    fun download(url: String) = liveData<Resource<File>>(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            val res = NetworkUtil.createService<FileIOApi>().downLoad2(url)
            val file = File(App.instance?.cacheDir, "temp")
            FileIOUtils.writeFileFromIS(file, res?.byteStream())
            emit(Resource.success(file))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.fail(data = null, message = e.message ?: "位置错误!"))
        }

    }


}

