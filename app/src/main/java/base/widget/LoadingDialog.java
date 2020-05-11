package base.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import skean.yzsm.com.framework.R;

/**
 * 加载中Dialog
 */
public class LoadingDialog extends Dialog {

    private TextView text;
    private ProgressBar progressBar;

    private Handler handler;

    private long mStartTime = -1;

    private boolean mPostedHide = false;

    private boolean mPostedShow = false;

    private boolean mDismissed = false;

    private static final int MIN_SHOW_TIME = 500; // ms
    private static final int MIN_DELAY = 500; // ms

    public LoadingDialog(Context context, String loadText, boolean cancelable) {
        super(context, android.R.style.Theme_Black_NoTitleBar);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.dimAmount = 0.0f;
        window.setAttributes(params);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setContentView(R.layout.dialog_loading);
        setCancelable(cancelable);
        text = (TextView) findViewById(R.id.txvLoadingText);
        progressBar = (ProgressBar) findViewById(R.id.pgbProgress);
        setLoadingText(loadText);
        handler = new Handler(context.getMainLooper());
    }

    public LoadingDialog setLoadingText(String loadingText) {
        text.setText(loadingText);
        return this;
    }

    public LoadingDialog setFinished(boolean finished) {
        progressBar.setVisibility(finished ? View.GONE : View.VISIBLE);
        return this;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks();
    }



    /**
     * Show the progress view after waiting for a minimum delay. If
     * during that time, hide() is called, the view is never made visible.
     */


    private void removeCallbacks() {
        handler.removeCallbacks(delayedShowTask);
        mPostedHide = false;
        handler.removeCallbacks(delayedHideTask);
        mPostedShow = false;
    }

    private final Runnable delayedHideTask = new Runnable() {

        @Override
        public void run() {
            mPostedHide = false;
            mStartTime = -1;
            hide();
        }
    };

    private final Runnable delayedShowTask = new Runnable() {

        @Override
        public void run() {
            mPostedShow = false;
            if (!mDismissed) {
                mStartTime = System.currentTimeMillis();
                show();
            }
        }
    };

}
