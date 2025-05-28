package me.skean.skeanframework.component;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * App的DialogFragment基类
 */
public class BaseDialogFragment extends DialogFragment {

    private boolean autoDismiss = true;
    private int customAnimation = -1;
    private int customStyle = -1;
    private int customTheme = -1;
    private DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onDismissListener;
    private DialogInterface.OnCancelListener onCancelListener;
    private DialogInterface.OnShowListener onShowListener;

    ///////////////////////////////////////////////////////////////////////////
    // 生命周期
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param style 样式,可选值 STYLE_NORMAL, STYLE_NO_TITLE, STYLE_NO_FRAME, STYLE_NO_INPUT 参考{@link DialogFragment}里面的定义
     * @param theme 主题
     */
    public void setCustomStyle(int style, @StyleRes int theme) {
        this.customStyle = style;
        this.customTheme = theme;
    }

    /**
     * 点击确定时候是否dismiss对话框
     *
     * @param autoDismiss 是否
     */
    public void setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
    }

    /**
     * 设置弹出的动画, 注意这个styleId是在style中的两项设置的id
     *
     * @param styleId styId, 具体需要的两个item为 "android:windowEnterAnimation" 和 "android:windowExitAnimation"
     */
    public void setCustomAnimation(@StyleRes int styleId) {
        customAnimation = styleId;
    }

    public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (customStyle >= 0) {
            if (customTheme < 0) customTheme = 0;
            setStyle(customStyle, customTheme);
        }
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        dialog.setOnShowListener(onShowListener);
        if (dialog instanceof AlertDialog) {
            ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                onButtonClick(dialog, DialogInterface.BUTTON_POSITIVE);
            });
            ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(v -> {
                onButtonClick(dialog, DialogInterface.BUTTON_NEGATIVE);
            });
        }
        if (window != null) {
            if (customAnimation >= 0) window.setWindowAnimations(customAnimation);
        }
        return dialog;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (onCancelListener != null) onCancelListener.onCancel(dialog);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) onDismissListener.onDismiss(dialog);
    }

    private void onButtonClick(DialogInterface dialog, int which) {
        if (onClickListener != null) {
            onClickListener.onClick(dialog, which);
        }
        if (autoDismiss) {
            dismissNow();
        }
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing() && !isRemoving();
    }

    ///////////////////////////////////////////////////////////////////////////
    //  设置
    ///////////////////////////////////////////////////////////////////////////

    public void show(FragmentManager manager) {
        showNow(manager, this.toString());
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部方法
    ///////////////////////////////////////////////////////////////////////////

    private Scheduler io() {
        return Schedulers.io();
    }

    private Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }
}
