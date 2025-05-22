package me.skean.skeanframework.bindingext

import android.annotation.SuppressLint
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethods
import com.jakewharton.rxbinding4.view.clicks
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import me.skean.skeanframework.ktext.setOnClickFilterListener
import java.util.concurrent.TimeUnit

/**
 * Created by Skean on 2025/05/22.
 */
@BindingMethods(
    value = [
    ]
)
object ViewDbExt {

    @SuppressLint("CheckResult")
    @BindingAdapter("bindClickFilterEvent")
    @JvmStatic
    fun View.bindClickFilterEvent(onClick: (Any) -> Unit) {
        this.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribe(onClick)
    }
}