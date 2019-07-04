package skean.me.base.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import skean.me.base.widget.LoadingDialog;
import skean.yzsm.com.framework.R;

/**
 * 全屏Dialog基类
 */
public class FullDialog extends Dialog {

    private float dimAmount = 0.6f;

    public FullDialog(@NonNull Context context) {
        super(context);
    }

    public FullDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected FullDialog(@NonNull Context context, boolean cancelable, @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void setContentView(int layoutResID) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(layoutResID);
        setUpWindow();
    }

    @Override
    public void setContentView(@NonNull View view) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(view);
        setUpWindow();
    }

    @Override
    public void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(view, params);
        setUpWindow();
    }

    private void setUpWindow() {
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = dimAmount;
        window.setAttributes(params);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    public void setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.dimAmount = dimAmount;
        getWindow().setAttributes(params);
    }
}
