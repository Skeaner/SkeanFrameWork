package me.skean.framework.example.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import me.skean.framework.example.R
import me.skean.framework.example.databinding.MainActivityBinding
import me.skean.skeanframework.component.BaseActivity
import me.skean.skeanframework.component.BaseHostActivity
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