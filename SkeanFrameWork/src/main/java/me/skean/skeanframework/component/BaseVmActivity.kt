package me.skean.skeanframework.component

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.hgj.jetpackmvvm.ext.getVmClazz
import me.hgj.jetpackmvvm.ext.util.notNull
import me.hgj.jetpackmvvm.network.manager.NetState
import me.hgj.jetpackmvvm.network.manager.NetworkStateManager
import me.skean.skeanframework.ktext.injectLauncher

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: ViewModelActivity基类，把ViewModel注入进来了
 */
abstract class BaseVmActivity<VM : BaseVm> : BaseActivity() {

    lateinit var viewModel: VM
    val launcher: ActivityLauncher by injectLauncher()
    private var isFirst: Boolean = true

    protected open val layoutId: Int? = null

    /**
     * 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿  bug
     * 这里传入你想要延迟的时间，延迟时间可以设置比转场动画时间长一点 单位： 毫秒
     * 不传默认 300毫秒
     * @return Long
     */
    protected open val lazyLoadTime = 300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateView()
        init(savedInstanceState)
    }

    open fun onCreateView() {
        layoutId.notNull({
            setContentView(it)
        }, {
            throw RuntimeException("layoutId不能为空")
        })
    }

    override fun onResume() {
        super.onResume()
        onVisible()
    }

    private fun init(savedInstanceState: Bundle?) {
        viewModel = createViewModel()
        initView(savedInstanceState)
        registerUiChange()
        NetworkStateManager.instance.mNetworkStateCallback.observe(this, Observer {
            onNetworkStateChanged(it)
        })
        setUpObserver()
        initData()
    }

    /**
     * 网络变化监听 子类重写
     */
    open fun onNetworkStateChanged(netState: NetState) {}

    open fun initView(savedInstanceState: Bundle?) {}

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
    open fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this) as Class<VM>)
    }

    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            // 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿.
            lifecycleScope.launch {
                delay(lazyLoadTime)
                isFirst = false
                lazyLoadData()
            }
        }
    }


    /**
     * 注册UI 事件
     */
    private fun registerUiChange() {
        //显示弹窗
        viewModel.uiChange.loading.observe(this, Observer {
            handleLoadingEvent(it)
        })
    }

    /**
     * 将非该Activity绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
     * @param viewModels Array<out BaseViewModel>
     */
    protected fun addLoadingObserve(vararg viewModels: BaseVm) {
        viewModels.forEach { viewModel ->
            viewModel.uiChange.loading.observe(this, Observer {
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