package me.skean.framework.example.view

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.BaseDifferAdapter
import com.dylanc.viewbinding.BindingViewHolder
import com.dylanc.viewbinding.binding
import com.hi.dhl.binding.databind
import com.hi.dhl.binding.viewbind
import me.skean.framework.example.BR
import me.skean.framework.example.R
import me.skean.framework.example.databinding.TestMvvmActivityBinding
import me.skean.framework.example.databinding.TestMvvmItemBinding
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.framework.example.viewmodel.TestMvvmViewModel
import me.skean.skeanframework.component.BaseVmDbActivity
import me.skean.skeanframework.delegate.AppDifferAdapter
import me.skean.skeanframework.ktext.*

/**
 * Created by Skean on 2022/4/20.
 */
class TestMvvmActivity() : BaseVmDbActivity<TestMvvmViewModel, TestMvvmActivityBinding>() {


    private var itemAdapter = AppDifferAdapter.create<MovieInfo.Data, TestMvvmItemBinding>()


    override fun initView(savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        initActionBar(binding.toolbar)
        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
            addDividerItemDecoration()
        }
        binding.srlLoader.autoRefresh()
    }


    override fun createObserver() {
        viewModel.data.observe(this) {
            itemAdapter.submitList(it)
        }
    }


}
