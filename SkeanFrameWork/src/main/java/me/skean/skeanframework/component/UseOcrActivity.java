package me.skean.skeanframework.component;

import android.Manifest;
import android.content.Intent;

import com.blankj.utilcode.util.PermissionUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import skean.yzsm.com.easypermissiondialog.EasyPermissionDialog;

/**
 * 使用OCR功能的基础Activity
 */
public class UseOcrActivity extends BaseActivity {

    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    ///////////////////////////////////////////////////////////////////////////
    // 1
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // 2
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // 3
    ///////////////////////////////////////////////////////////////////////////

    protected final void userCameraAndExternalStorageWithPermissionCheck() {
        XXPermissions.with(this).permission(PERMISSIONS).request(new OnPermissionCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                if (allGranted) onUseOcr();

            }

            @Override
            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                EasyPermissionDialog.build(getThis()).permissions(permissions).show(doNotAskAgain, allow -> {
                    if (allow) userCameraAndExternalStorageWithPermissionCheck();
                });
            }
        });
    }

    protected final boolean hasCameraPermission() {
        return PermissionUtils.isGranted(PERMISSIONS);
    }

    public void onUseOcr() {
    }

}
