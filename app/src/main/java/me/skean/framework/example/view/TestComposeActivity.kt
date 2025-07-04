package me.skean.framework.example.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.skean.framework.example.R
import me.skean.framework.example.databinding.TestCompseActivityBinding
import me.skean.framework.example.ui.AppTheme
import me.skean.framework.example.viewmodel.TestComposeViewModel
import me.skean.skeanframework.component.BaseVmActivity
import me.skean.skeanframework.component.BaseVmVbActivity
import me.skean.skeanframework.composeui.PasswordTextField
import me.skean.skeanframework.composeui.PasswordTextField2
import me.skean.skeanframework.ktext.dp2px

/**
 * Created by Skean on 2025/06/26.
 */
class TestComposeActivity : BaseVmActivity<TestComposeViewModel>() {
    override fun initView(savedInstanceState: Bundle?) {
        setContent {
            AppTheme {
                TestComposePage(viewModel)
            }
        }
    }

    @Composable
    @Preview(showBackground = true, device = Devices.PHONE, showSystemUi = true)
    fun TestComposePagePreview() {
        AppTheme {
            TestComposePage(viewModel())
        }
    }

    @Composable
    fun TestComposePage(vm: TestComposeViewModel) {
        Column(modifier = Modifier.fillMaxHeight().fillMaxHeight().background(Color.LightGray)) {
            Row(
                modifier = Modifier.fillMaxHeight(0.2f).fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(20.dp)
                        .aspectRatio(1.0f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                )
                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                    Text(text = vm.userInfo.fullName, style = MaterialTheme.typography.h6)
                    Text(text = vm.userInfo.deptName, style = MaterialTheme.typography.subtitle1)
                }
            }
            Surface(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                color = Color(0xffF2F7FB),
                shape = RoundedCornerShape(12.dp, 12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.size(20.dp))
                    MinePageButton(me.skean.skeanframework.R.drawable.sfw_ic_action_add, "修改密码")
                    Spacer(Modifier.size(20.dp))
                    MinePageButton(me.skean.skeanframework.R.drawable.sfw_ic_action_add, "退出登录")
                }

            }
        }
    }

    @Composable
    fun MinePageButton(icon: Int, text: String, onClick: () -> Unit = {}) {
        Button(
            modifier = Modifier.padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            onClick = onClick
        ) {
            Icon(painterResource(icon), null)
            Text(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.button,
                text = text
            )
        }
    }


}
