package me.skean.skeanframework.bindingext

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethods
import coil3.load
import coil3.request.error
import coil3.request.placeholder

/**
 * Created by Skean on 2025/05/22.
 */
@BindingMethods(
    value = [
    ]
)
object ImageViewDbExt {

    @BindingAdapter(value = ["image", "placeHolder", "errHolder"], requireAll =false)
    @JvmStatic
    fun ImageView.setLoadImage(image: Any?, placeHolder: Drawable? = null, errHolder: Drawable? = null) {
        this.load(image) {
            placeholder(placeHolder)
            error(errHolder)
        }
    }

}