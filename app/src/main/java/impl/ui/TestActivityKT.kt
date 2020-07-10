package impl.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.OnActivityResult
import impl.component.App
import me.skean.skeanframework.component.BaseActivity
import me.skean.skeanframework.component.UpdateDialog
import me.skean.skeanframework.utils.ContentUtil
import me.skean.skeanframework.utils.ImageUtil
import skean.yzsm.com.framework.R
import java.io.File

/**
 * Created by Skean on 19/10/9.
 */
@EActivity(R.layout.activity_main)
class TestActivityKT : BaseActivity() {
    val REQUEST_GET_SINGLE_FILE = 1

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
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
                val file = File(App.getAppPicturesDirectory(), "compress.jpg")
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
        LogUtils.i("测试Log到文件:" + ContentUtil.dateTime(System.currentTimeMillis()));
//        val testDialog = TestDialog()
//        testDialog.setCustomAnimation(R.style.WindowBottomInOutStyle)
//        testDialog.isCancelable = false
//        testDialog.show(supportFragmentManager)
        UpdateDialog.show(this,
                          "1",
                          "测速更新",
                          "https://oss.pgyer.com/5e5f9a3446210fd9309d2fc952493566" + ".apk?auth_key=1594370627-2bf596890f980461d7fb1822f0af1060-0-d42c4680a95dfd25d690876eaee0920f&response-content" + "-disposition=attachment%3B+filename%3Dsmartinquest-1.0d%2528b1%2529-06230020.apk",
                          true)
    }

    fun getPathFromURI(contentUri: Uri): String {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(contentUri)

        // Split at colon, use second item in the array
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

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
}