package skean.me.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.androidannotations.api.sharedpreferences.EditorHelperExt;

import java.io.File;

import skean.me.base.component.AppApplication;
import skean.me.base.component.BaseActivity;
import skean.me.base.utils.ImageUtil;
import skean.yzsm.com.framework.R;

public class MainActivity extends BaseActivity {
    private static final int REQUEST_GET_SINGLE_FILE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        findViewById(R.id.txvSelect).setOnClickListener(v -> Matisse.from(MainActivity.this)
//                                                                    .choose(MimeType.of(MimeType.JPEG, MimeType.PNG), true)
//                                                                    .showSingleMediaType(true)
//                                                                    .capture(true)
//                                                                    .captureStrategy(new CaptureStrategy(true,
//                                                                                                         "skean.yzsm.com.framework.fileprovider"))
//                                                                    .countable(true)
//                                                                    .maxSelectable(1)
//                                                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//                                                                    .thumbnailScale(0.85f)
//                                                                    .imageEngine(new PicassoEngine())
//                                                                    .forResult(9));
        findViewById(R.id.txvSelect).setOnClickListener(v -> {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_SINGLE_FILE);
        });
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