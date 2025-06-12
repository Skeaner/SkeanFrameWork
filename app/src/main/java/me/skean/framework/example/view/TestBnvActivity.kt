package me.skean.framework.example.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import me.skean.framework.example.R
import me.skean.framework.example.component.AppService
import me.skean.framework.example.databinding.TestBnvActivityBinding
import me.skean.framework.example.databinding.TestMvvmActivityBinding
import me.skean.framework.example.viewmodel.TestBnvViewModel
import me.skean.skeanframework.component.BaseVmVbActivity
import me.skean.skeanframework.ktext.findChildNavController
import me.skean.skeanframework.ktext.findNavController
import me.skean.skeanframework.ktext.logI
import me.skean.skeanframework.ktext.request
import me.skean.skeanframework.ktext.setGone
import me.skean.skeanframework.ktext.setupWithNavController2
import skean.yzsm.com.easypermissiondialog.EasyPermissionDialog

/**
 * Created by Skean on 2025/05/27.
 */
class TestBnvActivity : BaseVmVbActivity<TestBnvViewModel, TestBnvActivityBinding>() {


    private val bottomNavFragments = setOf(R.id.fragment1, R.id.fragment2, R.id.fragment3)
    private val hideToolbarFragments = setOf(R.id.fragment31)

    private lateinit var navController: NavController

    @SuppressLint("RestrictedApi")
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.viewModel = viewModel
        val hostFragment = supportFragmentManager.findFragmentById(R.id.homeNavHost) as NavHostFragment
        navController = hostFragment.navController
        binding.bnvHomeMenu.setupWithNavController2(navController)
        binding.tbTitle.setupWithNavController(navController, AppBarConfiguration(bottomNavFragments))
        navController.addOnDestinationChangedListener { controller, destination, arg ->
            binding.bnvHomeMenu.setGone(!bottomNavFragments.contains(destination.id))
            binding.appBarLayout.setGone(hideToolbarFragments.contains(destination.id))
        }
    }


    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            navController.navigateUp()
        }

    }

    override fun initData() {
    }


}