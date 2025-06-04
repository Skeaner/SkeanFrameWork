package me.skean.skeanframework.bindingext

import android.annotation.SuppressLint
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethods
import com.jakewharton.rxbinding4.view.clicks
import me.skean.skeanframework.ktext.defaultObserver
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
    @BindingAdapter("onClickFilter")
    @JvmStatic
    fun View.bindClickFilterCallback(onClick: () -> Unit) {
        this.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribe { onClick() }
    }


    @BindingAdapter("backgroundRes")
    @JvmStatic
    fun View.bindBackgroundRes(resId: Int) {
        if (resId == 0) {
            return
        }
        setBackgroundResource(resId)
    }
}