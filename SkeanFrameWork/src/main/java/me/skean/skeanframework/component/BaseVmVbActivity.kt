package me.skean.skeanframework.component

import android.view.View
import androidx.viewbinding.ViewBinding
import me.hgj.jetpackmvvm.ext.inflateBindingWithGeneric

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: 包含 ViewModel 和 ViewBinding ViewModelActivity基类，把ViewModel 和 ViewBinding 注入进来了
 * 需要使用 ViewBinding 的清继承它
 */
abstract class BaseVmVbActivity<VM : BaseVm, VB : ViewBinding> : BaseVmActivity<VM>() {


    lateinit var binding: VB

    /**
     * 创建DataBinding
     */
    override fun initDataBind(): View? {
        binding = inflateBindingWithGeneric(layoutInflater)
        return binding.root

    }
}