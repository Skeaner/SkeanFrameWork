package me.skean.skeanframework.component

import androidx.annotation.IntDef
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModel
import com.trello.rxlifecycle4.LifecycleProvider
import com.trello.rxlifecycle4.LifecycleTransformer
import com.trello.rxlifecycle4.RxLifecycle
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import me.hgj.jetpackmvvm.callback.livedata.event.EventLiveData
import me.skean.skeanframework.model.ViewModelEvent
import me.skean.skeanframework.utils.SingleLiveEvent

/**
 * Created by Skean on 2025/05/23.
 */
open class BaseVm : ViewModel(), LifecycleProvider<ViewModelEvent> {


    open val uiChange: UiChange by lazy { UiChange() }

    /**
     * 内置封装好的可通知Activity/fragment 显示隐藏加载框 因为需要跟网络请求显示隐藏loading配套才加的，不然我加他个鸡儿加
     */
    open class UiChange {
        val loading by lazy { SingleLiveEvent<LoadingEvent>() }
    }


    data class LoadingEvent @JvmOverloads constructor(
        val showing: Boolean,
        val msg: String? = null,
        val cancelable: Boolean = true,
        val state: State = State.PROGRESSING,
        val autoDismissMillis: Long = 0
    ) {
        enum class State {
            PROGRESSING, SUCCESS, FAIl
        }
    }

    inner class ViewModelLifecycleOwner : LifecycleOwner, AutoCloseable {
        private val _lifecycle = LifecycleRegistry(this)

        override val lifecycle: Lifecycle get() = _lifecycle

        init {
            _lifecycle.currentState = Lifecycle.State.CREATED
        }

        override fun close() {
            _lifecycle.currentState = Lifecycle.State.DESTROYED
        }

    }

    val lifecycleOwner: LifecycleOwner = ViewModelLifecycleOwner().apply { addCloseable(this) }

    private val lifecycleSubject = BehaviorSubject.create<ViewModelEvent>().apply {
        onNext(ViewModelEvent.CREATED)
        addCloseable { onNext(ViewModelEvent.CLEARED) }
    }

    override fun lifecycle(): Observable<ViewModelEvent> = lifecycleSubject.hide()

    override fun <T : Any?> bindToLifecycle(): LifecycleTransformer<T> = bindUntilEvent(ViewModelEvent.CLEARED)

    override fun <T : Any?> bindUntilEvent(event: ViewModelEvent): LifecycleTransformer<T> =
        RxLifecycle.bindUntilEvent(lifecycleSubject, event)

}