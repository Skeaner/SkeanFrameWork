package impl.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.blankj.utilcode.util.ToastUtils
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.OnActivityResult
import impl.component.App
import me.skean.skeanframework.component.BaseActivity
import me.skean.skeanframework.component.UpdateDialog
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
//        val testDialog = TestDialog()
//        testDialog.setCustomAnimation(R.style.WindowBottomInOutStyle)
//        testDialog.isCancelable = false
//        testDialog.show(supportFragmentManager)
        UpdateDialog.show(this, "1", "测速更新", "https://oss.pgyer.com/5e5f9a3446210fd9309d2fc952493566" +
                ".apk?auth_key=1594090321-5be73409e80de07d08483c42d6ffbb9d-0-ef518fef0f677db2e2f1bc6f8a375fb1&response-content" +
                "-disposition=attachment%3B+filename%3Dsmartinquest-1.0d%2528b1%2529-06230020.apk", true)
    }

    fun getPathFromURI(contentUri: Uri): String {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(contentUri)

        // Split at colon, use second item in the array
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

        val column = arrayOf(MediaStore.Images.Media.DATA)

        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"

        val cursor = context.contentResolver
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, arrayOf(id), null)

        val columnIndex = cursor!!.getColumnIndex(column[0])

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }
        cursor.close()
        return filePath
    }
}