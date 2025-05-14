package me.skean.skeanframework.component;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;

import com.blankj.utilcode.util.PermissionUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.skean.skeanframework.BuildConfig;
import me.skean.skeanframework.R;

import me.skean.skeanframework.utils.Glide4Engine;
import skean.yzsm.com.easypermissiondialog.EasyPermissionDialog;

/**
 * 选择图片基础Activity
 */
public class SelectPictureActivity extends BaseActivity {

    public static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int REQUEST_PERMISSION = 98;
    private static final int REQUEST_CHOOSE_PICTURE = 99;

    protected List<String> selectedPicturePaths;
    protected List<Uri> selectedPictureUris;
    private int maxSelectCount = 1;
    private boolean rememberSelectedPictures = false;

    ///////////////////////////////////////////////////////////////////////////
    // 1
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_PICTURE) {
            if (resultCode == RESULT_OK) {
                selectedPictureUris = Matisse.obtainResult(data);
                selectedPicturePaths = Matisse.obtainPathResult(data);
                onSelectPictureResult(selectedPicturePaths);
            }
        }
        else if (requestCode == REQUEST_PERMISSION) {
            startSelectPictureWithPermissionCheck();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 3
    ///////////////////////////////////////////////////////////////////////////

    protected final void startSelectPictureWithPermissionCheck() {
        XXPermissions.with(this).permission(PERMISSIONS).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean allGranted) {
                if (allGranted) {
                    startSelectPicture();
                }
            }

            @Override
            public void onDenied(List<String> permissions, boolean doNotAskAgain) {
                if (doNotAskAgain) {
                    EasyPermissionDialog.build(getThis()).permissions(PERMISSIONS).typeNeverAsk(null).show();
                }
                else {
                    EasyPermissionDialog.build(getThis()).permissions(PERMISSIONS).typeTemporaryDeny(allow -> {
                        if (allow) {
                            startSelectPictureWithPermissionCheck();
                        }
                    }).show();
                }
            }
        });
    }

    public void onSelectPictureResult(List<String> pathList) {

    }

    protected void setMaxSelectCount(int maxSelectCount) {
        this.maxSelectCount = maxSelectCount;
    }

    protected void setRememberSelectedPictures(boolean rememberSelectedPictures) {
        this.rememberSelectedPictures = rememberSelectedPictures;
    }

    protected void clearSelectedPictures(String path) {
        if (CollectionUtils.isNotEmpty(selectedPicturePaths)) {
            int i = selectedPicturePaths.indexOf(path);
            if (i != -1) {
                selectedPictureUris.remove(i);
                selectedPicturePaths.remove(i);
            }
        }
    }

    public final void startSelectPicture() {
        Matisse.from(this)
               .choose(EnumSet.of(MimeType.JPEG, MimeType.PNG), false)
               .theme(R.style.Matisse_APP)
               .countable(true)
               .capture(true, true)
               .selectedUri(rememberSelectedPictures ? selectedPictureUris : new ArrayList<>())
               .captureStrategy(new CaptureStrategy(true, getPackageName() + ".fileprovider", "test"))
               .maxSelectable(maxSelectCount)
               .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
               .thumbnailScale(0.85f)
               .imageEngine(new Glide4Engine())    // for glide-V4
               .originalEnable(false)
               .autoHideToolbarOnSingleTap(true)
               .forResult(REQUEST_CHOOSE_PICTURE);
    }

}
