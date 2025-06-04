package me.skean.skeanframework.bindingext

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.R
import androidx.appcompat.widget.SearchView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethods


/**
 * Created by Skean on 2025/05/22.
 */
@BindingMethods(
    value = [
    ]
)
object SearchViewDbExt {

    @BindingAdapter(value = ["searchTextBackground"])
    @JvmStatic
    fun SearchView.bindSearchTextBackground(background: Drawable? = null) {

    }

}