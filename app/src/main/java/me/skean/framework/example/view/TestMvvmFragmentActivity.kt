package me.skean.framework.example.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dylanc.viewbinding.getBinding
import com.hi.dhl.binding.viewbind
import com.rxjava.rxlife.life
import me.skean.framework.example.R
import me.skean.framework.example.component.AppActivityLauncher
import me.skean.framework.example.databinding.TestMvvmActivityBinding
import me.skean.framework.example.databinding.TestMvvmFragmentActivityBinding
import me.skean.framework.example.databinding.TestMvvmItemBinding
import me.skean.framework.example.net.bean.MovieInfo
import me.skean.framework.example.viewmodel.TestMvvmViewModel
import me.skean.materialdialogs.MaterialDialog
import me.skean.skeanframework.component.BaseActivity
import me.skean.skeanframework.ktext.*

/**
 * Created by Skean on 2022/4/20.
 */
class TestMvvmFragmentActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_mvvm_fragment_activity)
    }

}
