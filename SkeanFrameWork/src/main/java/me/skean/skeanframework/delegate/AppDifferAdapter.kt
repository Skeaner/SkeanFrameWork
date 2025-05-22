package me.skean.skeanframework.delegate

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter4.BaseDifferAdapter
import com.dylanc.viewbinding.BindingViewHolder
import me.skean.skeanframework.BR
import me.skean.skeanframework.ktext.inflateBinding
import me.skean.skeanframework.ktext.queryGenericBindingClass
import me.skean.skeanframework.ktext.quickDiffCallback


/**
 * Created by Skean on 2025/05/21.
 */
abstract class AppDifferAdapter<T : Any, VB : ViewBinding>(differCallback: DiffUtil.ItemCallback<T>? = null, items: List<T>? = null) :
    BaseDifferAdapter<T, BindingViewHolder<VB>>(differCallback ?: quickDiffCallback(), items.orEmpty()) {

    companion object {
        @JvmStatic
        @JvmOverloads
        inline fun <T : Any, reified VB : ViewBinding> create(
            differCallback: DiffUtil.ItemCallback<T>? = null,
            items: List<T>? = null
        ): AppDifferAdapter<T, VB> {
            return object : AppDifferAdapter<T, VB>(differCallback, items) {
                init {
                    vbClass = VB::class.java
                }
            }
        }
    }

    lateinit var vbClass: Class<VB>

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int, item: T?) {
        if (holder.binding is ViewDataBinding) {
            (holder.binding as ViewDataBinding).setVariable(BR._item, item)
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
        if (!this::vbClass.isInitialized) {
            vbClass = queryGenericBindingClass(this, 1)
        }
        return BindingViewHolder(inflateBinding<VB>(vbClass, context, parent))
    }


}