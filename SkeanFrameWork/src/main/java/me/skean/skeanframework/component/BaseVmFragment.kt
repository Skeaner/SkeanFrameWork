package me.skean.skeanframework.component

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.getVmClazz
import me.hgj.jetpackmvvm.network.manager.NetState
import me.hgj.jetpackmvvm.network.manager.NetworkStateManager
import me.skean.skeanframework.ktext.injectLauncher

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: ViewModelFragment基类，自动把ViewModel注入Fragment
 */

abstract class BaseVmFragment<VM : BaseVm> : BaseFragment() {


    //是否第一次加载
    private var isFirst: Boolean = true

    lateinit var viewModel: VM
    val launcher: ActivityLauncher by injectLauncher()


    /**
     * 当前Fragment绑定的视图布局
     */
    protected open val layoutId: Int? = null

    /**
     * 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿  bug
     * 这里传入你想要延迟的时间，延迟时间可以设置比转场动画时间长一点 单位： 毫秒
     * 不传默认 300毫秒
     * @return Long
     */
    protected open val lazyLoadTime = 300L

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutId?.let { inflater.inflate(it, container, false) }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFirst = true
        viewModel = createViewModel()
        initView(savedInstanceState)
        registerDefUIChange()
        setUpObserver()
        initData()
    }

    override fun onResume() {
        super.onResume()
        onVisible()
    }


    /**
     * 网络变化监听 子类重写
     */
    open fun onNetworkStateChanged(netState: NetState) {}

    /**
     * 初始化view
     */
    open fun initView(savedInstanceState: Bundle?) {}

    /**
     * 设置观察者
     */
    open fun setUpObserver() {}

    /**
     * Fragment执行onCreate后触发的方法
     */
    open fun initData() {}

    /**
     * 懒加载
     */
    open fun lazyLoadData() {}


    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this) as Class<VM>)
    }

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            // 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿
            lifecycleScope.launch {
                delay(lazyLoadTime)
                isFirst = false
                lazyLoadData()
                NetworkStateManager.instance.mNetworkStateCallback.observe(
                    this@BaseVmFragment,
                    Observer {
                        //不是首次订阅时调用方法，防止数据第一次监听错误
                        if (!isFirst) {
                            onNetworkStateChanged(it)
                        }
                    })
            }
        }
    }


    /**
     * 注册 UI 事件
     */
    private fun registerDefUIChange() {
        viewModel.uiChange.loading.observe(viewLifecycleOwner, Observer {
            handleLoadingEvent(it)
        })
    }

    /**
     * 将非该Fragment绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
     * @param viewModels Array<out BaseViewModel>
     */
    protected fun addLoadingObserve(vararg viewModels: BaseVm) {
        viewModels.forEach { viewModel ->
            //显示弹窗
            viewModel.uiChange.loading.observe(viewLifecycleOwner, Observer {
                handleLoadingEvent(it)
            })
        }
    }

    private fun handleLoadingEvent(ev: BaseVm.LoadingEvent) {
        if (ev.showing) {
            when (ev.state) {
                BaseVm.LoadingEvent.State.PROGRESSING -> {
                    showLoading(ev.msg ?: "请稍后", ev.cancelable)
                }

                BaseVm.LoadingEvent.State.SUCCESS -> {
                    showLoaded(true, ev.msg ?: "成功", ev.cancelable)
                }

                BaseVm.LoadingEvent.State.FAIl -> {
                    showLoaded(false, ev.msg ?: "失败", ev.cancelable)
                }
            }
            if (ev.autoDismissMillis > 0) {
                dismissLoadingDelayed(ev.autoDismissMillis)
            }
        } else {
            dismissLoading()
        }
    }


}