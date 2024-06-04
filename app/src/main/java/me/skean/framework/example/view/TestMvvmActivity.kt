package me.skean.framework.example.view

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.BaseDifferAdapter
import com.dylanc.viewbinding.BindingViewHolder
import me.goldze.mvvmhabit.base.BaseActivity
import me.skean.framework.example.BR
import me.skean.framework.example.R
import me.skean.framework.example.component.AppActivityLauncher
import me.skean.framework.example.databinding.TestMvvmActivityBinding
import me.skean.framework.example.databinding.TestMvvmItemBinding
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.framework.example.viewmodel.TestMvvmViewModel
import me.skean.skeanframework.ktext.*

/**
 * Created by Skean on 2022/4/20.
 */
class TestMvvmActivity() : BaseActivity<TestMvvmActivityBinding, TestMvvmViewModel>() {

    private val launcher: AppActivityLauncher by injectLauncher()
    private lateinit var itemAdapter: ItemAdapter


    override fun initContentView(savedInstanceState: Bundle?) = R.layout.test_mvvm_activity

    override fun initVariableId() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initData() {
        initActionBar(binding.toolbar)
        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ItemAdapter().apply { itemAdapter = this }
            addDividerItemDecoration()
        }
        binding.srlLoader.autoRefresh()
    }

    override fun initViewObservable() {
        viewModel.uc.finishRefreshing.observe(this) { isSuccessAndNoMore ->
            binding.srlLoader.finishRefresh(0, isSuccessAndNoMore.first, isSuccessAndNoMore.second)
        }
        viewModel.uc.finishLoadMore.observe(this) { isSuccessAndNoMore ->
            binding.srlLoader.finishLoadMore(0, isSuccessAndNoMore.first, isSuccessAndNoMore.second)
        }
        viewModel.data.observe(this) {
            itemAdapter.submitList(it)
        }
    }


    private inner class ItemAdapter :
        BaseDifferAdapter<MovieInfo.Data, BindingViewHolder<TestMvvmItemBinding>>(itemDiffCallback, emptyList()) {
        override fun onBindViewHolder(holder: BindingViewHolder<TestMvvmItemBinding>, position: Int, item: MovieInfo.Data?) {
            holder.binding.item = item
        }


        override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int) =
            BindingViewHolder(TestMvvmItemBinding.inflate(layoutInflater, parent, false))
    }


    private val itemDiffCallback = object : DiffUtil.ItemCallback<MovieInfo.Data>() {
        override fun areItemsTheSame(oldItem: MovieInfo.Data, newItem: MovieInfo.Data) = oldItem == newItem

        override fun areContentsTheSame(oldItem: MovieInfo.Data, newItem: MovieInfo.Data) = oldItem == newItem

    }


}
