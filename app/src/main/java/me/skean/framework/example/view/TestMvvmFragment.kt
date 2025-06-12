package me.skean.framework.example.view

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import me.skean.framework.example.databinding.TestMvvmActivityBinding
import me.skean.framework.example.databinding.TestMvvmItemBinding
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.framework.example.viewmodel.MainViewModel
import me.skean.framework.example.viewmodel.TestMvvmViewModel
import me.skean.skeanframework.component.BaseVmDbFragment
import me.skean.skeanframework.delegate.AppDifferAdapter
import me.skean.skeanframework.ktext.*

/**
 * Created by Skean on 2022/4/20.
 */
class TestMvvmFragment() : BaseVmDbFragment<TestMvvmViewModel, TestMvvmActivityBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel>()

    private var itemAdapter = AppDifferAdapter.create<MovieInfo.Data, TestMvvmItemBinding>()


    override fun initView(savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
            addDividerItemDecoration()
        }
        binding.srlLoader.autoRefresh()
    }


    override fun setUpObserver() {
        viewModel.data.observe(this) {
            itemAdapter.submitList(it)
        }
    }


}
