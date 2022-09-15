package me.skean.skeanframework.rx;

import androidx.lifecycle.ViewModel
import com.trello.rxlifecycle3.LifecycleProvider
import com.trello.rxlifecycle3.LifecycleTransformer
import com.trello.rxlifecycle3.RxLifecycle
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

abstract class RxViewModel : ViewModel(), LifecycleProvider<RxViewModel.ViewModelEvent> {

    enum class ViewModelEvent {
        ACTIVE,
        CLEARED
    }

    private val lifecycleSubject = BehaviorSubject.create<ViewModelEvent>().apply {
        onNext(ViewModelEvent.ACTIVE)
    }

    override fun lifecycle(): Observable<ViewModelEvent> {
        return lifecycleSubject.hide()
    }

    override fun <T : Any?> bindUntilEvent(event: ViewModelEvent): LifecycleTransformer<T> {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event)
    }

//    override fun <T : Any?> bindToLifecycle(): LifecycleTransformer<T> {
//        return RxLifecycleViewModel.bindViewModel(lifecycleSubject)
//    }

    override fun <T : Any?> bindToLifecycle(): LifecycleTransformer<T> {
        return bindUntilEvent(ViewModelEvent.CLEARED)
    }

    override fun onCleared() {
        lifecycleSubject.onNext(ViewModelEvent.CLEARED)
        super.onCleared()
    }

}
