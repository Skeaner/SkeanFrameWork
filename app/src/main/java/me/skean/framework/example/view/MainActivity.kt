package me.skean.framework.example.view

import android.os.Bundle
import me.skean.framework.example.databinding.MainActivityBinding
import me.skean.framework.example.viewmodel.MainViewModel
import me.skean.skeanframework.component.BaseVmVbActivity

/**
 * Created by Skean on 2025/05/26.
 */
class MainActivity : BaseVmVbActivity<MainViewModel, MainActivityBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}