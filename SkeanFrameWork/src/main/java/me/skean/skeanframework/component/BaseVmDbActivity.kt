package me.skean.skeanframework.component

import android.view.View
import androidx.databinding.ViewDataBinding
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.inflateBindingWithGeneric

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: 包含ViewModel 和Databind ViewModelActivity基类，把ViewModel 和Databind注入进来了
 * 需要使用Databind的清继承它
 */
abstract class BaseVmDbActivity<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmActivity<VM>() {


    lateinit var binding: DB

    /**
     * 创建DataBinding
     */
    override fun initDataBind(): View? {
        binding = inflateBindingWithGeneric(layoutInflater)
        return binding.root
    }
}