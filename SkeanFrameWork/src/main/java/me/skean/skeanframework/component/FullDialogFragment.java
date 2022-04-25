package me.skean.skeanframework.component;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * 全屏DialogFragment
 */
public class FullDialogFragment extends BaseDialogFragment {

    private float dimAmount = 0.6f;
    private int gravity = Gravity.CENTER;
    private int width = WindowManager.LayoutParams.MATCH_PARENT;
    private int height = WindowManager.LayoutParams.MATCH_PARENT;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = dimAmount;
        params.gravity = gravity;
        window.setAttributes(params);
        window.setLayout(width, height);
    }

    public BaseDialogFragment setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
        return this;
    }

    public FullDialogFragment setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public FullDialogFragment setLayout(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }
}
