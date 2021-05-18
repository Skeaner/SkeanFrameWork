package me.skean.framework.example.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.OnActivityResult
import me.skean.framework.example.component.App
import me.skean.skeanframework.component.BaseActivity
import me.skean.skeanframework.utils.ImageUtil
import me.skean.framework.example.R
import me.skean.framework.example.db.dao.DummyDao
import me.skean.framework.example.db.entity.Dummy
import me.skean.framework.example.db.pojo.NameCount
import me.skean.framework.example.event.BackgroundEvent
import me.skean.framework.example.event.ForegroundEvent
import me.skean.skeanframework.component.ActivityStarter
import me.skean.skeanframework.rx.DefaultObserver
import me.skean.skeanframework.rx.DefaultSingleObserver
import org.greenrobot.eventbus.Subscribe
import java.io.File

import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by Skean on 19/10/9.
 */
@EActivity(R.layout.activity_main)
class TestActivity : BaseActivity() {

    companion object : ActivityStarter() {
        fun start(host: Any) {
            start(host, Intent(getContextFromHost(host), TestActivity::class.java), null)
        }
    }

    val REQUEST_GET_SINGLE_FILE = 1

    private var dummyDao: DummyDao? = null
    private var count = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dummyDao = App.instance?.database?.dummyDao
        postInMainDelayed(3000, "MSG", TestRunnable())
    }

    private inner class TestRunnable : Runnable {
        override fun run() {
            ToastUtils.showShort("测试第${count++}次")
            postInMainDelayed(3000, "MSG", TestRunnable())
        }
    }

    @OnActivityResult(1)
    fun resultGet(resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            var selectedImageUri = data.data
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

    @Click
    fun txvSelectClicked() {
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
}