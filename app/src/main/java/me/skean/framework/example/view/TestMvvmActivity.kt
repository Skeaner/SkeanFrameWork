package me.skean.framework.example.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dylanc.viewbinding.getBinding
import com.hi.dhl.binding.viewbind
import com.rxjava.rxlife.life
import me.skean.framework.example.R
import me.skean.framework.example.component.AppActivityLauncher
import me.skean.framework.example.databinding.TestMvvmActivityBinding
import me.skean.framework.example.databinding.TestMvvmItemBinding
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.framework.example.viewmodel.TestMvvmViewModel
import me.skean.skeanframework.component.BaseActivity
import me.skean.skeanframework.ktext.*

/**
 * Created by Skean on 2022/4/20.
 */
class TestMvvmActivity() : BaseActivity() {

    private val vb: TestMvvmActivityBinding by viewbind()
    private val launcher: AppActivityLauncher by launcher()
    private val vm: TestMvvmViewModel by viewModels()
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    private fun initViews() {
        vb.apply {
            useHomeShowTitle = false
            setSupportActionBar(toolbar)
            itemAdapter = ItemAdapter()
            itemAdapter.setOnItemClickListener { adapter, view, position ->
//                requestPermissionEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE){
//                    Matisse.from(this@TestMvvmActivity)
//                        .choose(EnumSet.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF), false)
//                        .theme(R.style.Matisse_APP)
//                        .countable(true)
//                        .maxSelectable(1)
//                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//                        .thumbnailScale(0.85f)
//                        .imageEngine(CoilEngine()) // for glide-V4
//                        .originalEnable(false)
//                        .autoHideToolbarOnSingleTap(true)
//                        .forResult(1)
//                }
                launcher.launch<TestActivity>{ code, data ->
                    ToastUtils.showShort("返回了")
                }
            }
            rvItems.layoutManager = LinearLayoutManager(context)
            rvItems.adapter = itemAdapter
            rvItems.addDividerItemDecoration()
            srlLoader.setOnRefreshListener { load(true) }
            srlLoader.setOnLoadMoreListener { load(false) }
            srlLoader.autoRefresh()
        }
    }

    private fun load(refresh: Boolean) {
        vm.requestData(refresh)
                .life(this)
                .subscribe(defaultSingleObserver {
                    vb.srlLoader.finishLoad(it)
                    if (refresh) itemAdapter.setNewInstance(it.result) else itemAdapter.addData(it.result.orEmpty())
                    if (!it.success) ToastUtils.showShort(it.msg)
                })
    }

    @SuppressLint("CheckResult")
    private inner class ItemAdapter : BaseQuickAdapter<MovieInfo, BaseViewHolder>(R.layout.test_mvvm_item, mutableListOf()) {
        override fun convert(holder: BaseViewHolder, item: MovieInfo) {
            holder.getBinding<TestMvvmItemBinding>().apply {
                tvTitle.text = "${holder.bindingAdapterPosition + 1}. ${item.data?.first()?.name}"
                tvDesc.text = item.data?.first()?.description
                ivIcon.load(item.data?.first()?.poster) {
                    placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher)
                }
            }
        }

    }

}
