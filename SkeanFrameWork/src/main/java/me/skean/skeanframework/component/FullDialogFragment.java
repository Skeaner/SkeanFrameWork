package me.skean.skeanframework.component;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * 全屏DialogFragment
 */
public class FullDialogFragment extends BaseDialogFragment {

    private float dimAmount = 0.6f;
    private int gravity = Gravity.CENTER;
    private int width = WindowManager.LayoutParams.MATCH_PARENT;
    private int height = WindowManager.LayoutParams.MATCH_PARENT;

    public FullDialogFragment() {
        setCustomStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = dimAmount;
            params.gravity = gravity;
            window.setAttributes(params);
            window.setLayout(width, height);
        }
        return dialog;
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
