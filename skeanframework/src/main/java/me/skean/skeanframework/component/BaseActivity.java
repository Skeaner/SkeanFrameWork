package me.skean.skeanframework.component;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.skean.skeanframework.R;
import me.skean.skeanframework.widget.LoadingDialog;

/**
 * App的Activity基类 <p/>
 */
@SuppressWarnings("unused")
public class BaseActivity extends RxAppCompatActivity {
    protected Bundle savedInstanceStateCache;
    protected Context context = null;
    protected ActionBar actionBar;
    protected LoadingDialog loadingDialog;

    private Handler mainHandler;

    protected boolean useHomeAsBack = true;
    protected boolean useHomeShowTitle = true;
    protected boolean isMenuCreated = false;
    protected boolean backControl = false;


    ///////////////////////////////////////////////////////////////////////////
    // 声明周期/初始化/设置
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceStateCache = savedInstanceState;
        context = this;
        initActionBar();
        mainHandler = new Handler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        isMenuCreated = true;
        return result;
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        initActionBar();
    }

    /**
     * 初始化Actionbar
     */
    /**
     * 初始化Actionbar
     */
    protected void initActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(useHomeAsBack);
            actionBar.setDisplayShowTitleEnabled(useHomeShowTitle);
        }
    }

    public void setUseHomeAsBack(boolean useHomeAsBack) {
        this.useHomeAsBack = useHomeAsBack;
        initActionBar();
    }

    /**
     * 返回键的行为 <p/>
     *
     * @return true:当前Activity执行了返回键相关操作,  false:当前Activity没有执行任何操作, 将会执行默认的操作(如finish())
     */
    public boolean onBack() {
        return actionBar != null && collapseActionView(actionBar);
    }

    @Override
    public void onBackPressed() {
        if (backControl) {
            if (!onBack()) finish();
        }
        else {
            super.onBackPressed();
        }
    }

    protected boolean collapseActionView(ActionBar actionBar) {
        boolean result = false;
        try {
            result = (boolean) ActionBar.class.getMethod("collapseActionView").invoke(actionBar);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && useHomeAsBack) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        onViewCreated();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        onViewCreated();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        onViewCreated();
    }

    protected void onViewCreated() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // 进度框便利方法
    ///////////////////////////////////////////////////////////////////////////

    public void showLoading(boolean cancelable) {
        showLoading(R.string.loading, cancelable);
    }

    public void showLoading(int stringId, boolean cancelable) {
        showLoading(getString(stringId), cancelable);
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
        getLoadingDialog().setLoadingText(getString(resId));
    }

    public void dismissLoading() {
        getLoadingDialog().dismiss();
    }

    public void dismissLoadingDelayed(long millis) {
        mainHandler.removeCallbacks(dismissTask);
        mainHandler.postDelayed(dismissTask, millis);
    }

    public Runnable dismissTask = () -> getLoadingDialog().dismiss();

    private LoadingDialog getLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = getLoadingDialog(getString(R.string.loading), true).setFinished(false);
        }
        return loadingDialog;
    }

    private LoadingDialog getLoadingDialog(String text, boolean cancelable) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context, text, cancelable);
        }
        else {
            loadingDialog.setLoadingText(text).setCancelable(cancelable);
        }
        return loadingDialog;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 状态获取/上下文相关
    ///////////////////////////////////////////////////////////////////////////

    public <T extends Application> T getApp() {
        return (T) getApplication();
    }

    public Context getContext() {
        return context;
    }

    public Handler getMainHandler() {
        return mainHandler;
    }

    public boolean isMenuCreated() {
        return isMenuCreated;
    }

    public BaseActivity getThis() {
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Snack便捷方法
    ///////////////////////////////////////////////////////////////////////////

    public void snack(View parent, String text) {
        Snackbar snackbar = Snackbar.make(parent, text, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void snack(View parent, String text, int duration) {
        Snackbar snackbar = Snackbar.make(parent, text, duration);
        snackbar.show();
    }

    public void snackTop(View parent, String text, int duration) {
        Snackbar snackbar = Snackbar.make(parent, text, duration);
        final long millis = duration == Snackbar.LENGTH_SHORT ? 1500 : 2750;
        View view = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        view.setVisibility(View.INVISIBLE);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onShown(final Snackbar sb) {
                sb.getView().setVisibility(View.VISIBLE);
                sb.getView().postDelayed(() -> sb.getView().setVisibility(View.GONE), millis);
            }

        });
        snackbar.show();
    }


    ///////////////////////////////////////////////////////////////////////////
    // 便利方法
    ///////////////////////////////////////////////////////////////////////////

    public void postInMain(Runnable r) {
        mainHandler.post(r);
    }

    public void postInMainDelayed(Runnable r, long millis) {
        mainHandler.postDelayed(r, millis);
    }

    /**
     * 隐藏软键盘 <p/>
     *
     * @return 是否有执行隐藏软键盘的操作
     */
    public boolean hideSoftKeyboard() {
        InputMethodManager kbManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                return kbManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return false;
    }

    /**
     * 展示软键盘
     * <p/>
     * * @return 是否有执行展示软键盘的操作
     */
    public boolean showSoftKeyboard(EditText target) {
        InputMethodManager kbManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        target.requestFocus();
        return kbManager.showSoftInput(target, 0);
    }

    /**
     * 展示软键盘, 并且光标移到最后面
     * * @return 是否有执行展示软键盘的操作
     */
    public boolean showSoftKeyboardAndMoveToEnd(EditText target) {
        InputMethodManager kbManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        target.requestFocus();
        target.setSelection(target.length());
        return kbManager.showSoftInput(target, 0);
    }

    ///////////////////////////////////////////////////////////////////////////
    // RX的便捷方法
    ///////////////////////////////////////////////////////////////////////////

    protected Scheduler io() {
        return Schedulers.io();
    }

    protected Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

}
