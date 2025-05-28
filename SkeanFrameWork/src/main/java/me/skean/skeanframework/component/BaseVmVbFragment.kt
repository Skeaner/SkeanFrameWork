package me.skean.skeanframework.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import me.hgj.jetpackmvvm.ext.inflateBindingWithGeneric

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: ViewModelFragment基类，自动把ViewModel注入Fragment和 ViewBinding 注入进来了
 * 需要使用 ViewBinding 的清继承它
 */
abstract class BaseVmVbFragment<VM : BaseVm, VB : ViewBinding> : BaseVmFragment<VM>() {


    //该类绑定的 ViewBinding
    lateinit var binding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = initDataBind(inflater, container, savedInstanceState)
        return binding.root
    }

    /**
     * 创建ViewBinding
     */
    open fun initDataBind(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): VB {
        return inflateBindingWithGeneric(layoutInflater, container, false)
    }

}