package me.skean.framework.example.view

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import me.skean.framework.example.databinding.AboutFragmentBinding
import me.skean.framework.example.viewmodel.AboutViewModel
import me.skean.framework.example.viewmodel.MainViewModel
import me.skean.skeanframework.component.BaseVmDbFragment
import me.skean.skeanframework.ktext.setOnClickFilterListener

/**
 * Created by Skean on 2025/05/26.
 */
class AboutFragment : BaseVmDbFragment<AboutViewModel, AboutFragmentBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun initView(savedInstanceState: Bundle?) {
        binding.btn2.setOnClickFilterListener {
//            findNavController().navigate(AboutFragmentDirections.actionAboutToHome())
            showLoading(true)
            dismissLoadingDelayed(2000)
        }
    }

    override fun initData() {
        binding.viewModel = viewModel
    }
}