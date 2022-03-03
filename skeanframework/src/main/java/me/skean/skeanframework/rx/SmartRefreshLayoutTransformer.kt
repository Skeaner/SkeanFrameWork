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
package me.skean.skeanframework.rx

import com.scwang.smart.refresh.layout.SmartRefreshLayout
import io.reactivex.*
import org.reactivestreams.Publisher

/**
 * Transformer that continues a subscription until a second Observable emits an event.
 */
class SmartRefreshLayoutTransformer<T>(private val loader: SmartRefreshLayout,
                                       private val refresh: Boolean,
                                       private val checkSuccessAndNoMore: (T) -> Pair<Boolean, Boolean>) :
        ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T> {

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.doOnNext { finishLoading(true, it) }.doOnError { finishLoading(false, null) }
    }

    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream.doOnNext { finishLoading(true, it) }.doOnError { finishLoading(false, null) }
    }

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.doOnSuccess { finishLoading(true, it) }.doOnError { finishLoading(false, null) }
    }

    private fun finishLoading(success: Boolean, t: T?) {
        if (success) {
            val result = checkSuccessAndNoMore.invoke(t!!)
            if (refresh) {
                loader.finishRefresh(0, result.first, if (result.first) result.second else true)
            } else {
                loader.finishLoadMore(0, result.first, if (result.first) result.second else false)
            }
        } else {
            if (refresh) {
                loader.finishRefresh(0, false, true)
            } else {
                loader.finishLoadMore(0, false, false)
            }
        }
    }

}