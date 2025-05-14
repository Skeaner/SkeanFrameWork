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
 * 使用相机和读取储存功能的基础Activity
 */
public class UseCameraAndExternalStorageActivity extends BaseActivity {

    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSION = 99;

    ///////////////////////////////////////////////////////////////////////////
    // 1
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION) {
            userCameraAndExternalStorageWithPermissionCheck();

        }
    }

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
                if (allGranted) onUseCameraAndExternalStorage();
            }

            @Override
            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                if (doNotAskAgain) {
                    EasyPermissionDialog.build(getThis()).permissions(PERMISSIONS).typeNeverAsk(null).show();
                }
                else {
                    EasyPermissionDialog.build(getThis()).permissions(PERMISSIONS).typeTemporaryDeny(allow -> {
                        if (allow) userCameraAndExternalStorageWithPermissionCheck();
                    }).show();
                }
            }
        });
    }

    public void onUseCameraAndExternalStorage() {
    }

    protected final boolean hasCameraPermission() {
        return PermissionUtils.isGranted(PERMISSIONS);
    }

}
