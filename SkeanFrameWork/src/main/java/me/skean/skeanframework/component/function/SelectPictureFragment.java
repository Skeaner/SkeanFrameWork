package me.skean.skeanframework.component.function;

import android.Manifest;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import me.skean.skeanframework.component.BaseFragment;
import me.skean.skeanframework.utils.GlideEngine;
import skean.yzsm.com.easypermissiondialog.EasyPermissionDialog;

/**
 * 选择图片基础Activity
 */
public class SelectPictureFragment extends BaseFragment {

    protected List<LocalMedia> selectedPictures = new ArrayList<>();
    private int maxSelectCount = 1;
    private boolean rememberSelectedPictures = false;

    ///////////////////////////////////////////////////////////////////////////
    // 1
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // 3
    ///////////////////////////////////////////////////////////////////////////

    protected final void startSelectPictureWithPermissionCheck() {
        XXPermissions.with(this)
                     .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                     .request(new OnPermissionCallback() {
                         @Override
                         public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                             if (allGranted) {
                                 startSelectPicture();
                             }
                         }

                         @Override
                         public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                             EasyPermissionDialog.build(getThis()).permissions(permissions).show(doNotAskAgain, allow -> {
                                 if (allow) startSelectPictureWithPermissionCheck();
                             });
                         }
                     });
    }

    public void onSelectPictureResult(List<LocalMedia> pathList) {

    }

    protected void setMaxSelectCount(int maxSelectCount) {
        this.maxSelectCount = maxSelectCount;
    }

    protected void setRememberSelectedPictures(boolean rememberSelectedPictures) {
        this.rememberSelectedPictures = rememberSelectedPictures;
    }

    protected void clearSelectedPictures(int position) {
        if (CollectionUtils.isNotEmpty(selectedPictures)) {
            if (position != -1) {
                selectedPictures.remove(position);
            }
        }
    }

    public final void startSelectPicture() {
        PictureSelector.create(this)
                       .openGallery(SelectMimeType.ofImage())
                       .setImageEngine(GlideEngine.createGlideEngine())
                       .isDisplayCamera(true)
                       .setSelectedData(rememberSelectedPictures ? selectedPictures : new ArrayList<>())
                       .setMaxSelectNum(maxSelectCount)
                       .forResult(new OnResultCallbackListener<>() {
                           @Override
                           public void onResult(ArrayList<LocalMedia> result) {
                               selectedPictures = result;
                               onSelectPictureResult(selectedPictures);
                           }

                           @Override
                           public void onCancel() {

                           }
                       });
    }

}
