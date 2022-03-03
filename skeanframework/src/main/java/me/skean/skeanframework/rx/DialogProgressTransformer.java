/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.skean.skeanframework.rx;

import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import me.skean.skeanframework.widget.LoadingDialog2;

import static com.trello.rxlifecycle3.internal.Preconditions.checkNotNull;

/**
 * Transformer that continues a subscription until a second Observable emits an event.
 */
public final class DialogProgressTransformer<T> implements ObservableTransformer<T, T>, FlowableTransformer<T, T>, SingleTransformer<T, T> {
    final Observable<?> observable;
    final LoadingDialog2 dialog;

    public DialogProgressTransformer(LoadingDialog2 dialog, Observable<?> observable) {
        checkNotNull(observable, "observable == null");
        this.observable = observable;
        this.dialog = dialog;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.doOnSubscribe(disposable -> dialog.show()).takeUntil(observable).doOnTerminate(dialog::dismiss);
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream.doOnSubscribe(disposable -> dialog.show())
                       .takeUntil(observable.toFlowable(BackpressureStrategy.LATEST))
                       .doOnTerminate(dialog::dismiss);
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.doOnSubscribe(disposable -> dialog.show()).takeUntil(observable.firstOrError()).doOnTerminate(dialog::dismiss);
    }

}
