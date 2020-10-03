package me.skean.skeanframework.component;

import android.app.Dialog;
import android.content.DialogInterface;

import com.trello.rxlifecycle3.components.support.RxDialogFragment;

import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * App的DialogFragment基类
 */
public class BaseDialogFragment extends RxDialogFragment {

    private boolean autoDismiss = true;
    private boolean useCustomAnimation = false;
    private int customAnimation;
    private DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onDismissListener;
    private DialogInterface.OnCancelListener onCancelListener;
    private DialogInterface.OnShowListener onShowListener;

    ///////////////////////////////////////////////////////////////////////////
    // 生命周期
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onStart() {
        super.onStart();
        final Dialog dialog = getDialog();
        //修改动画
        if (useCustomAnimation) dialog.getWindow().setWindowAnimations(customAnimation);
        if (dialog instanceof AlertDialog) {
            ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                onButtonClick(dialog, DialogInterface.BUTTON_POSITIVE);
            });
            ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(v -> {
                onButtonClick(dialog, DialogInterface.BUTTON_NEGATIVE);
            });
        }
        dialog.setOnCancelListener(onCancelListener);
        dialog.setOnShowListener(onShowListener);
        dialog.setOnDismissListener(onDismissListener);
    }

    private void onButtonClick(DialogInterface dialog, int which) {
        if (onClickListener != null) {
            onClickListener.onClick(dialog, which);
        }
        if (autoDismiss) {
            dismissAllowingStateLoss();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //  设置
    ///////////////////////////////////////////////////////////////////////////

    public void show(FragmentManager manager) {
        super.show(manager, getClass().getSimpleName());
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
        useCustomAnimation = true;
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
