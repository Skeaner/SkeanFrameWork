package skean.me.base.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import skean.yzsm.com.framework.R;

/**
 * 权限设置Dialog
 */

public class PermissionSettingDialog {
    private Context context;
    private Object container;
    private MaterialDialog.Builder builder;
    private int requestCode;

    public static PermissionSettingDialog build(Activity activity, int requestCode) {
        return new PermissionSettingDialog(activity, activity, requestCode);
    }

    public static PermissionSettingDialog build(Fragment fragment, int requestCode) {
        return new PermissionSettingDialog(fragment, fragment.getActivity(), requestCode);
    }

    public static PermissionSettingDialog build(android.app.Fragment fragment, int requestCode) {
        return new PermissionSettingDialog(fragment, fragment.getActivity(), requestCode);
    }

    private PermissionSettingDialog(Object container, Context context, int requestCode) {
        this.container = container;
        this.context = context;
        this.requestCode = requestCode;
        builder = new MaterialDialog.Builder(context);
        builder.title(R.string.permissionApplyFail)
               .content(R.string.defaultPermissionNeverGrantedMessage)
               .positiveText(R.string.goSetting)
               .onPositive(new MaterialDialog.SingleButtonCallback() {
                   @Override
                   public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                       startForResult();
                   }
               })
               .negativeText(R.string.cancelToSetting)
               .neutralText(R.string.neverToSetting);
    }

    public MaterialDialog.Builder getBuilder() {
        return builder;
    }

    public PermissionSettingDialog title(String title) {
        this.builder.title(title);
        return this;
    }

    public PermissionSettingDialog title(int titleRes) {
        this.builder.title(titleRes);
        return this;
    }

    public PermissionSettingDialog content(String content) {
        this.builder.content(content);
        return this;
    }

    public PermissionSettingDialog content(int contentRes) {
        this.builder.content(contentRes);
        return this;
    }

    public PermissionSettingDialog positiveText(String text) {
        this.builder.positiveText(text);
        return this;
    }

    public PermissionSettingDialog positiveText(int textRes) {
        this.builder.positiveText(textRes);
        return this;
    }

    public PermissionSettingDialog negativeText(String text) {
        this.builder.negativeText(text);
        return this;
    }

    public PermissionSettingDialog negativeText(int textRes) {
        this.builder.negativeText(textRes);
        return this;
    }

    public PermissionSettingDialog onNegative(MaterialDialog.SingleButtonCallback callback) {
        this.builder.onNegative(callback);
        return this;
    }

    public PermissionSettingDialog neverText(String text) {
        this.builder.neutralText(text);
        return this;
    }

    public PermissionSettingDialog neverText(int textRes) {
        this.builder.neutralText(textRes);
        return this;
    }

    public PermissionSettingDialog onNever(MaterialDialog.SingleButtonCallback callback) {
        this.builder.onNeutral(callback);
        return this;
    }

    public PermissionSettingDialog theme(Theme theme) {
        this.builder.theme(theme);
        return this;
    }

    public MaterialDialog build() {
        return this.builder.build();
    }

    public void show() {
        this.builder.show();
    }

    private void startForResult() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        if (container instanceof Activity) {
            ((Activity) container).startActivityForResult(intent, requestCode);
        } else if (container instanceof Fragment) {
            ((Fragment) container).startActivityForResult(intent, requestCode);
        } else if (container instanceof android.app.Fragment) {
            ((android.app.Fragment) container).startActivityForResult(intent, requestCode);
        }
    }

}
