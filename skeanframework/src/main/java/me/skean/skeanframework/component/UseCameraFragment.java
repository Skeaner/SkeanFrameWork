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
 * 使用相机的基础
 */
@RuntimePermissions
public class UseCameraFragment extends BaseFragment {

    private static final String P = Manifest.permission.CAMERA;
    private static final int REQUEST_PERMISSION = 99;

    ///////////////////////////////////////////////////////////////////////////
    // 1
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UseCameraFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION) {
            UseCameraFragmentPermissionsDispatcher.startCameraWithPermissionCheck(this);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 2
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // 3
    ///////////////////////////////////////////////////////////////////////////

    protected final void startCameraWithPermissionCheck() {
        UseCameraFragmentPermissionsDispatcher.startCameraWithPermissionCheck(this);
    }

    protected final boolean hasCameraPermission() {
        return PermissionUtils.hasSelfPermissions(getContext(), P);
    }

    @NeedsPermission({P})
    public  void startCamera() {
    }

    @OnPermissionDenied({P})
    public final void permissionDenied() {
        if (PermissionUtils.hasSelfPermissions(getContext(), P)) {
            EasyPermissionDialog.build(this).permissions(P).typeTemporaryDeny(allow -> {
                if (allow) {
                    UseCameraFragmentPermissionsDispatcher.startCameraWithPermissionCheck(this);
                }
            }).show();
        }
    }

    @OnNeverAskAgain({P})
    public final void permissionNever() {
        EasyPermissionDialog.build(this).permissions(P).typeNeverAsk(REQUEST_PERMISSION, null).show();
    }

}
