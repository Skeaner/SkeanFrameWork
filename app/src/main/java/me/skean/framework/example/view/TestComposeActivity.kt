package me.skean.framework.example.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.king.ultraswiperefresh.NestedScrollMode
import com.king.ultraswiperefresh.UltraSwipeRefresh
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshFooter
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshHeader
import com.king.ultraswiperefresh.rememberUltraSwipeRefreshState
import me.skean.framework.example.ui.AppTheme
import me.skean.framework.example.viewmodel.TestComposeViewModel
import me.skean.skeanframework.component.BaseVmActivity

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

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun TestComposePage(vm: TestComposeViewModel) {
        val state = rememberUltraSwipeRefreshState()
        var noMore by remember { vm.isNoMoreState }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("豆瓣", modifier = Modifier.fillMaxWidth().padding(end = 12.dp), textAlign = TextAlign.Center)
                    },
                )
            }
        ) {
            UltraSwipeRefresh(
                state = state,
                onRefresh = {
                },
                onLoadMore = {
                },
                loadMoreEnabled = !noMore,
                modifier = Modifier.background(color = Color(0x7FEEEEEE)),
                headerScrollMode = NestedScrollMode.Translate,
                footerScrollMode = NestedScrollMode.Translate,
                headerIndicator = {
                    ClassicRefreshHeader(it)
                },
                footerIndicator = {
                    ClassicRefreshFooter(it)
                }
            ) {
                LazyColumn(Modifier.background(color = Color.White)) {
                }
            }
        }
    }


}
