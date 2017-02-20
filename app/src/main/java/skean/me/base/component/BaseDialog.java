package skean.me.base.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import skean.me.base.widget.LoadingDialog;
import skean.yzsm.com.framework.R;

public class BaseDialog extends Dialog {

    protected LoadingDialog loadingDialog;
    protected Handler mainHandler;
    private Toast toast;

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainHandler = new Handler();
        toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
    }

    protected void setUpWindow(float dimAmount, int windowAnim) {
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = dimAmount;
        window.setAttributes(params);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        if (windowAnim != 0) window.setWindowAnimations(windowAnim);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 进度框便利方法
    ///////////////////////////////////////////////////////////////////////////

    public void showLoading(boolean cancelable) {
        showLoading(R.string.loading, cancelable);
    }

    public void showLoading(int stringId, boolean cancelable) {
        showLoading(getContext().getString(stringId), cancelable);
    }

    public void showLoading(String text, boolean cancelable) {
        getLoadingDialog(text, cancelable).setFinished(false).show();
    }

    public void setLoaded() {
        getLoadingDialog().setFinished(true).setLoadingText("");
    }

    public void setLoaded(String text) {
        getLoadingDialog().setFinished(true).setLoadingText(text);
    }

    public void setLoadingText(String text) {
        getLoadingDialog().setLoadingText(text);
    }

    public void setLoadingText(int resId) {
        getLoadingDialog().setLoadingText(getContext().getString(resId));
    }

    public void dismissLoading() {
        getLoadingDialog().dismiss();
    }

    public void dismissLoadingDelayed(long millis) {
        mainHandler.removeCallbacks(dismissTask);
        mainHandler.postDelayed(dismissTask, millis);
    }

    public Runnable dismissTask = new Runnable() {
        @Override
        public void run() {
            getLoadingDialog().dismiss();
        }
    };

    private LoadingDialog getLoadingDialog() {
        if (loadingDialog == null) loadingDialog = getLoadingDialog(getContext().getString(R.string.loading), true).setFinished(false);
        return loadingDialog;
    }

    private LoadingDialog getLoadingDialog(String text, boolean cancelable) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getContext(), text, cancelable);
        } else {
            loadingDialog.setLoadingText(text).setCancelable(cancelable);
        }
        return loadingDialog;
    }

    ///////////////////////////////////////////////////////////////////////////
    // toast的便捷方法
    ///////////////////////////////////////////////////////////////////////////

    public Toast getToast() {
        return toast;
    }

    public void toast(int stringId, int toastLength) {
        toast.setText(stringId);
        toast.setDuration(toastLength);
        toast.show();
    }

    public void toast(String text, int toastLength) {
        toast.setText(text);
        toast.setDuration(toastLength);
        toast.show();

    }

    public void toast(int stringId) {
        toast.setText(stringId);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();

    }

    public void toast(String text) {
        toast.setText(text);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public void toastFormat(String text, Object... args) {
        String content = String.format(text, args);
        toast(content);
    }

    public void toastFormat(@StringRes int resId, Object... args) {
        String content = getContext().getString(resId, args);
        toast(content);
    }

}
