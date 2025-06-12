package me.skean.framework.example.view

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import me.skean.framework.example.databinding.Fragment1Binding
import me.skean.framework.example.viewmodel.Fragment1ViewModel
import me.skean.skeanframework.component.BaseVmVbFragment
import me.skean.skeanframework.ktext.setOnClickFilterListener

/**
 * Created by Skean on 2025/06/12.
 */
class Fragment1: BaseVmVbFragment<Fragment1ViewModel, Fragment1Binding>() {

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.btn.setOnClickFilterListener {
            findNavController().navigate(Fragment1Directions.navToFragment11())
        }
    }
}