package me.skean.skeanframework.bindingext

import androidx.appcompat.widget.SearchView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethods
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import me.skean.skeanframework.ktext.setOnCloseBtnClickListener
import me.skean.skeanframework.ktext.setOnQueryTextListener


/**
 * Created by Skean on 2025/05/22.
 */
@BindingMethods(
    value = [
    ]
)
object SearchViewDbExt {

    @BindingAdapter(value = ["submitText"])
    @JvmStatic
    fun SearchView.setSubmitText(submitText: CharSequence? = null) {
        setQuery(submitText, true)
    }

    @InverseBindingAdapter(attribute = "submitText", event = "submitTextAttrChanged")
    @JvmStatic
    fun SearchView.getSubmitText(): CharSequence {
        return query
    }

    @BindingAdapter(value = ["temporaryText"])
    @JvmStatic
    fun SearchView.setTemporaryText(temporaryText: CharSequence? = null) {
        setQuery(temporaryText, true)
    }

    @InverseBindingAdapter(attribute = "temporaryText", event = "temporaryTextAttrChanged")
    @JvmStatic
    fun SearchView.getTemporaryText(): CharSequence {
        return query
    }


    @BindingAdapter(value = ["submitTextAttrChanged", "temporaryTextAttrChanged"], requireAll = false)
    @JvmStatic
    fun SearchView.setTextChangedListener(
        submitTextChanged: InverseBindingListener? = null,
        changedTextChanged: InverseBindingListener? = null
    ) {
        setOnCloseBtnClickListener {
            setQuery(null, true)
            clearFocus()
            submitTextChanged?.onChange()
            changedTextChanged?.onChange()
        }
        setOnQueryTextListener(onQueryTextSubmit = {
            submitTextChanged?.onChange()
            clearFocus()
            false
        }, onQueryTextChange = {
            changedTextChanged?.onChange()
            false
        })
    }


}