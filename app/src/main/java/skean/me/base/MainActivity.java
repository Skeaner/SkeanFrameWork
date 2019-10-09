package skean.me.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.api.sharedpreferences.EditorHelperExt;

import java.io.File;

import skean.me.base.component.AppApplication;
import skean.me.base.component.BaseActivity;
import skean.me.base.utils.ImageUtil;
import skean.yzsm.com.framework.R;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    private static final int REQUEST_GET_SINGLE_FILE = 1;

    @Click
    void txvSelectClicked(){
        TestDialog testDialog = new TestDialog();
        testDialog.setCustomAnimation(R.style.WindowBottomInOutStyle);
        testDialog.setCancelable(false);
        testDialog.show(getSupportFragmentManager());

}

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_GET_SINGLE_FILE) {
                    Uri selectedImageUri = data.getData();
                    // Get the path from the Uri
                    final String path = getPathFromURI(selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                        File file = new File(AppApplication.getAppPicturesDirectory(), "compress.jpg");
                        ImageUtil.Compressor.toActualSizeFile(getContext(), f, file, 50, 800, 550, new ImageUtil.Compressor.FileCallBack() {
                            @Override
                            public void onSuccess(File file) {
                                ToastUtils.showShort("成功");
                            }

                            @Override
                            public void onFail() {
                                ToastUtils.showShort("失败");
                            }
                        });
                    }
                    // Set the image in ImageView
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver()
                               .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
}