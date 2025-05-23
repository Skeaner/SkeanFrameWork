package me.skean.skeanframework.component

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.trello.rxlifecycle4.LifecycleProvider
import com.trello.rxlifecycle4.LifecycleTransformer
import com.trello.rxlifecycle4.RxLifecycle
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.skean.skeanframework.model.ViewModelEvent

/**
 * Created by Skean on 2025/05/23.
 */
open class BaseVm : BaseViewModel(), LifecycleProvider<ViewModelEvent> {

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