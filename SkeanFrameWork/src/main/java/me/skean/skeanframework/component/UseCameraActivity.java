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
 * 使用相机的基础
 */
public class UseCameraActivity extends BaseActivity {

    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private static final int REQUEST_PERMISSION = 99;

    ///////////////////////////////////////////////////////////////////////////
    // 1
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION) {
            startCameraWithPermissionCheck();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 2
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // 3
    ///////////////////////////////////////////////////////////////////////////

    protected final void startCameraWithPermissionCheck() {
        XXPermissions.with(this).permission(PERMISSIONS).request(new OnPermissionCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                if (allGranted) startCamera();
            }

            @Override
            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                if (doNotAskAgain) {
                    EasyPermissionDialog.build(getThis()).permissions(PERMISSIONS).typeNeverAsk(null).show();
                }
                else {
                    EasyPermissionDialog.build(getThis()).permissions(PERMISSIONS).typeTemporaryDeny(allow -> {
                        if (allow) startCameraWithPermissionCheck();
                    }).show();
                }
            }
        });
    }

    protected final boolean hasCameraPermission() {
        return PermissionUtils.isGranted(PERMISSIONS);
    }

    public void startCamera() {
    }

}
