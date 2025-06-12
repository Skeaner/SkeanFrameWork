package me.skean.skeanframework.bindingext

import android.content.res.ColorStateList
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethods
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import me.skean.skeanframework.model.RefreshFinishEvent

/**
 * Created by Skean on 2023/9/21.
 */
@BindingMethods(
    value = [
    ]
)
object BottomNavigationViewBDbExt {

    @BindingAdapter(value = ["itemIconTintList"])
    @JvmStatic
    fun BottomNavigationView.setItemIconTintList2(itemIconTintList: ColorStateList?) {
        this.itemIconTintList = itemIconTintList
    }


}

