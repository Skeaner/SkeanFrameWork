package me.skean.skeanframework.bindingext

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethods
import coil.load

/**
 * Created by Skean on 2025/05/22.
 */
@BindingMethods(
    value = [
    ]
)
object ImageViewDbExt {

    @BindingAdapter(value = ["image", "placeHolder", "errHolder"])
    @JvmStatic
    fun ImageView.bindLoadImage(image: Any?, placeHolder: Drawable?, errHolder: Drawable?) {
        this.load(image) {
            placeHolder?.let { placeholder(it) }
            errHolder?.let { error(errHolder) }
        }
    }
}