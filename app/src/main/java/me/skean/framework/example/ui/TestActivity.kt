package me.skean.framework.example.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.blankj.utilcode.util.ToastUtils
import com.hi.dhl.binding.viewbind
import io.reactivex.Single
import me.skean.framework.example.component.App
import me.skean.skeanframework.component.BaseActivity
import me.skean.skeanframework.utils.ImageUtil
import me.skean.framework.example.databinding.ActivityTestBinding
import me.skean.framework.example.db.dao.DummyDao
import me.skean.framework.example.event.BackgroundEvent
import me.skean.framework.example.event.ForegroundEvent
import me.skean.skeanframework.component.ActivityStarter
import me.skean.skeanframework.ktext.*
import me.skean.skeanframework.net.FileIOApi
import me.skean.skeanframework.utils.NetworkUtil
import org.greenrobot.eventbus.Subscribe
import org.koin.android.ext.android.inject
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Created by Skean on 19/10/9.
 */
class TestActivity : BaseActivity() {

    companion object : ActivityStarter() {
        fun start(host: Any) {
            start(host, Intent(getContextFromHost(host), TestActivity::class.java), null)
        }
    }

    val REQUEST_GET_SINGLE_FILE = 1

    private val dummyDao: DummyDao by inject()
    private var count = 0

    private val vb: ActivityTestBinding by viewbind()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb.srlLoader.setEnableLoadMore(true)
        vb.srlLoader.setEnableRefresh(true)
        vb.srlLoader.setOnRefreshListener {
            testRefresh(true)
        }
        vb.srlLoader.setOnLoadMoreListener {
            testRefresh(false)
        }
        vb.txvSelect.setOnClickListener {
            val baseUrl = NetworkUtil.getBaseUrlForClass(FileIOApi::class.java)
            ToastUtils.showShort(baseUrl)
            throw  RuntimeException("测试上报")
        }
//        postInMainDelayed(3000, "MSG", TestRunnable())
        val dm = resources.displayMetrics
        vb.tvInfo.text = "Resolution:${dm.widthPixels}X${dm.heightPixels}\nDPI:${dm.density * 160f.toInt()}"
    }

    private inner class TestRunnable : Runnable {
        override fun run() {
            ToastUtils.showShort("测试第${count++}次")
            postInMainDelayed(3000, "MSG", TestRunnable())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            var selectedImageUri = data?.data
            // Get the path from the Uri
            val path = getPathFromURI(selectedImageUri!!)
            if (path != null) {
                val f = File(path)
                selectedImageUri = Uri.fromFile(f)
                val file = File(App.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "compress.jpg")
                ImageUtil.Compressor.toActualSizeFile(getContext(), f, file, 50, 800, 550, object : ImageUtil.Compressor.FileCallBack {
                    override fun onSuccess(file: File) {
                        ToastUtils.showShort("成功")
                    }

                    override fun onFail() {
                        ToastUtils.showShort("失败")
                    }
                })
            }
        }
    }


    private fun txvSelectClicked() {
        removeMainCallbacksAndMessages("MSG")
//        val item = Dummy().apply { fullName = "测试" }
//        dummyDao?.let {
//            it.saveAll(item)
//                .subscribeOn(io())
//                .observeOn(mainThread())
//                .subscribe(object : DefaultSingleObserver<List<Long>>() {
//                    override fun onSuccess2(t: List<Long>) {
//                        ToastUtils.showShort("保存成功")
//                    }
//                })
//            it.countName()
//                .subscribeOn(io())
//                .observeOn(mainThread())
//                .subscribe(object : DefaultObserver<List<NameCount>>() {
//                    override fun onNext2(t: List<NameCount>) {
//                        ToastUtils.showShort("查询成功")
//                    }
//                })
//        }
//        LogUtils.i("测试Log到文件:" + ContentUtil.dateTime(System.currentTimeMillis()))
//        TestDialog().setGravity(Gravity.BOTTOM)
//                .setLayout( WindowManager.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(320f))
//                .setDimAmount(0.4f)
//                .apply { this.isCancelable = true }
//                .apply { this.setCustomAnimation(R.style.WindowBottomInOutStyle) }
//                .show(supportFragmentManager)
//        UpdateDialog.show(this,
//                          "1",
//                          "测速更新",
//                          "https://oss.pgyer.com/5e5f9a3446210fd9309d2fc952493566" + ".apk?auth_key=1594370627-2bf596890f980461d7fb1822f0af1060-0-d42c4680a95dfd25d690876eaee0920f&response-content" + "-disposition=attachment%3B+filename%3Dsmartinquest-1.0d%2528b1%2529-06230020.apk",
//                          true)

    }

    fun getPathFromURI(contentUri: Uri): String {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(contentUri)

        // Split at colon, use second item in the array
        val id = wholeID.split(":".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()[1]

        val column = arrayOf(MediaStore.Images.Media.DATA)

        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"

        val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, arrayOf(id), null)

        val columnIndex = cursor!!.getColumnIndex(column[0])

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }
        cursor.close()
        return filePath
    }

    @Subscribe
    fun getBackgroundEvent(event: BackgroundEvent) {

    }

    @Subscribe
    fun getForegroundEvent(event: ForegroundEvent) {

    }

    private fun testUploadFile() {
        Single.timer(4, TimeUnit.SECONDS)
            .subscribeOnIoObserveOnMainThread()
            .applyAutoLoading(this) {
                ToastUtils.showShort("取消了")
            }
            .subscribe(defaultSingleObserver {
                ToastUtils.showShort("完毕")
            })

    }


    private fun testRefresh(refresh: Boolean) {
        if (refresh) count = 0
        Single.timer(1, TimeUnit.SECONDS)
            .subscribeOnIoObserveOnMainThread()
            .applyAutoRefresh(vb.srlLoader, refresh) {
                return@applyAutoRefresh true to (count > 2)
            }
            .subscribe(defaultSingleObserver {
                count++
            })

    }

    private fun testPermission(onGranted: () -> Unit) {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        requestPermissionEach(*permissions) {
            onGranted.invoke()
        }
    }
}