package me.skean.skeanframework.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

/**
 * 全屏Dialog基类
 */
public class FullDialog extends Dialog {

    protected float dimAmount = 0.6f;
    private boolean useCustomAnimation = false;
    private int customAnimation;
    private int gravity = Gravity.CENTER;
    private int width = WindowManager.LayoutParams.MATCH_PARENT;
    private int height = WindowManager.LayoutParams.MATCH_PARENT;

    public FullDialog(@NonNull Context context) {
        super(context);
    }

    public FullDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected FullDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpWindow();
    }

    private void setUpWindow() {
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = dimAmount;
        params.gravity = gravity;
        window.setAttributes(params);
        window.setLayout(width, height);
    }

    public void setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.dimAmount = dimAmount;
        getWindow().setAttributes(params);
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }


    public void setLayout(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 设置弹出的动画, 注意这个styleId是在style中的两项设置的id
     *
     * @param resId resId, 具体需要的两个item为 "android:windowEnterAnimation" 和 "android:windowExitAnimation"
     */
    public void setCustomAnimation(@StyleRes int resId) {
        useCustomAnimation = true;
        customAnimation = resId;
        getWindow().setWindowAnimations(resId);
    }
}
