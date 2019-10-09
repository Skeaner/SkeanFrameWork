package skean.me.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_main.view.*
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.OnActivityResult
import skean.me.base.component.AppApplication
import skean.me.base.component.BaseActivity
import skean.me.base.utils.ImageUtil
import skean.yzsm.com.framework.R
import java.io.File

/**
 * Created by Skean on 19/10/9.
 */
@EActivity(R.layout.activity_main)
class MainActivityKT : BaseActivity() {
    val REQUEST_GET_SINGLE_FILE = 1

    @OnActivityResult(1)
    fun resultGet(resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            var selectedImageUri = data.data
            // Get the path from the Uri
            val path = getPathFromURI(selectedImageUri)
            if (path != null) {
                val f = File(path)
                selectedImageUri = Uri.fromFile(f)
                val file = File(AppApplication.getAppPicturesDirectory(), "compress.jpg")
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
        val testDialog = TestDialog()
        testDialog.setCustomAnimation(R.style.WindowBottomInOutStyle)
        testDialog.isCancelable = false
        testDialog.show(supportFragmentManager)
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