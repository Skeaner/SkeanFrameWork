package me.skean.skeanframework.component;

import android.Manifest;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.PermissionUtils;
import permissions.dispatcher.RuntimePermissions;
import skean.yzsm.com.easypermissiondialog.EasyPermissionDialog;

/**
 * 使用OCR功能的基础Activity
 */
@RuntimePermissions
public class UseOcrActivity extends BaseActivity {

    private static final String P1 = Manifest.permission.CAMERA;
    private static final String P2 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String P3 = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String P4 = Manifest.permission.READ_PHONE_STATE;
    private static final int REQUEST_PERMISSION = 99;

    ///////////////////////////////////////////////////////////////////////////
    // 1
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UseOcrActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION) {
            UseOcrActivityPermissionsDispatcher.onUseOcrWithPermissionCheck(this);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 2
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // 3
    ///////////////////////////////////////////////////////////////////////////

    protected final void userCameraAndExternalStorageWithPermissionCheck() {
        UseOcrActivityPermissionsDispatcher.onUseOcrWithPermissionCheck(this);
    }

    protected final boolean hasCameraPermission() {
        return PermissionUtils.hasSelfPermissions(this, P1, P2, P3, P4);
    }

    @NeedsPermission({P1, P2, P3, P4})
    public void onUseOcr() {
    }

    @OnPermissionDenied({P1, P2, P3, P4})
    public final void permissionDenied() {
        if (PermissionUtils.hasSelfPermissions(getContext(), P1, P2, P3, P4)) {
            EasyPermissionDialog.build(this).permissions(P1, P2, P3, P4).typeTemporaryDeny(allow -> {
                if (allow) {
                    UseOcrActivityPermissionsDispatcher.onUseOcrWithPermissionCheck(this);
                }
            }).show();
        }
    }

    @OnNeverAskAgain({P1, P2, P3, P4})
    public final void permissionNever() {
        EasyPermissionDialog.build(this).permissions(P1, P2, P3, P4).typeNeverAsk( null).show();
    }

}
