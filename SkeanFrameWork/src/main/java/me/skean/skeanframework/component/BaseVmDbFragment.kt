package me.skean.skeanframework.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import me.hgj.jetpackmvvm.ext.inflateBindingWithGeneric

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: ViewModelFragment基类，自动把ViewModel注入Fragment和Databind注入进来了
 * 需要使用Databind的清继承它
 */
abstract class BaseVmDbFragment<VM : BaseVm, DB : ViewDataBinding> : BaseVmFragment<VM>() {


    //该类绑定的ViewDataBinding
    lateinit var binding: DB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = initDataBind(inflater, container, savedInstanceState)
        return binding.root
    }

    /**
     * 创建DataBinding
     */
    open fun initDataBind(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): DB {
        return inflateBindingWithGeneric(layoutInflater, container, false)
    }
}