package me.skean.skeanframework.component

import android.view.View
import androidx.databinding.ViewDataBinding
import me.hgj.jetpackmvvm.ext.inflateBindingWithGeneric

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: 包含ViewModel 和Databind ViewModelActivity基类，把ViewModel 和Databind注入进来了
 * 需要使用Databind的清继承它
 */
abstract class BaseVmDbActivity<VM : BaseVm, DB : ViewDataBinding> : BaseVmActivity<VM>() {


    lateinit var binding: DB
    val isBindingInitialized get() = ::binding.isInitialized

    override fun onCreateView() {
        binding = initDataBind()
        setContentView(binding.root)
    }

    /**
     * 创建DataBinding
     */
    open fun initDataBind(): DB {
        return inflateBindingWithGeneric(layoutInflater)
    }
}