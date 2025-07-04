package me.skean.framework.example.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.skean.skeanframework.component.BaseVm

/**
 * Created by Skean on 2025/06/11.
 */
class TestComposeViewModel : BaseVm() {
    data class UserInfo(val fullName: String, val deptName: String)

    var userInfo by mutableStateOf(UserInfo("用户", "部门"))


    fun newUser() {
        userInfo = UserInfo("新用户", "新部门")
    }

}