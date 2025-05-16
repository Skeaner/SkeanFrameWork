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
public class UseCameraFragment extends BaseFragment {

    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA};

    ///////////////////////////////////////////////////////////////////////////
    // 1
    ///////////////////////////////////////////////////////////////////////////



    ///////////////////////////////////////////////////////////////////////////
    // 2
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // 3
    ///////////////////////////////////////////////////////////////////////////

    protected final void startCameraWithPermissionCheck() {
        XXPermissions.with(this)
                .permission(PERMISSIONS)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                       if (allGranted) startCamera();
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        EasyPermissionDialog.build(getThis()).permissions(permissions).show(doNotAskAgain, allow -> {
                            if (allow) startCameraWithPermissionCheck();
                        });
                    }
                });
    }

    protected final boolean hasCameraPermission() {
        return PermissionUtils.isGranted( PERMISSIONS);
    }

    public  void startCamera() {
    }


}
