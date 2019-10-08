package skean.me.base.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
