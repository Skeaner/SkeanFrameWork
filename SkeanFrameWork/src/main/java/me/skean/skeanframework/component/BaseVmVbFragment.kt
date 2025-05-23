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

    override fun layoutId() = 0

    //该类绑定的 ViewBinding
    private var _vb: VB? = null
    val binding: VB get() = _vb!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _vb  = inflateBindingWithGeneric(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _vb = null
    }
}