package me.skean.skeanframework.component.function;

import android.Manifest;

import com.blankj.utilcode.util.PermissionUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import androidx.annotation.NonNull;
import me.skean.skeanframework.component.BaseActivity;
import skean.yzsm.com.easypermissiondialog.EasyPermissionDialog;

/**
 * 使用相机和读取储存功能的基础Activity
 */
public class UseCameraAndExternalStorageActivity extends BaseActivity {

    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
                if (allGranted) onUseCameraAndExternalStorage();
            }

            @Override
            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                EasyPermissionDialog.build(getThis()).permissions(permissions).show(doNotAskAgain, allow -> {
                    if (allow) userCameraAndExternalStorageWithPermissionCheck();
                });
            }
        });
    }

    public void onUseCameraAndExternalStorage() {
    }

    protected final boolean hasCameraPermission() {
        return PermissionUtils.isGranted(PERMISSIONS);
    }

}
