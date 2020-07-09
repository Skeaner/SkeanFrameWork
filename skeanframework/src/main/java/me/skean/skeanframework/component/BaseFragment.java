package me.skean.skeanframework.component;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxFragment;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.skean.skeanframework.widget.LoadingDialog;

/**
 * App的DialogFragment基类 <p/>
 */
@SuppressWarnings("unused")
public abstract class BaseFragment extends RxFragment {
    protected Bundle savedInstanceStateCache;
    protected BaseActivity hostActivity;
    private Context context;
    protected LoadingDialog loadingDialog;

    protected boolean isMenuCreated;

    ///////////////////////////////////////////////////////////////////////////
    // 设置/生命周期/初始化
    ///////////////////////////////////////////////////////////////////////////

    public BaseFragment() {
    }

    public float getFragmentIndex() {
        throw new RuntimeException("使用前必须要复写该方法");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceStateCache = savedInstanceState;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.hostActivity = (BaseActivity) activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        isMenuCreated = true;
    }

    public boolean onBack() {
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 状态获取/上下文相关
    ///////////////////////////////////////////////////////////////////////////

    public boolean isMenuCreated() {
        return isMenuCreated;
    }

    public <T extends Application> T getApp() {
        return hostActivity.getApp();
    }

    public BaseActivity getHostActivity() {
        return hostActivity;
    }

    public BaseFragment getThis() {
        return this;
    }

    public Handler getMainHandler() {
        return hostActivity.getMainHandler();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 进度框便利方法
    ///////////////////////////////////////////////////////////////////////////

    public void showLoading(boolean cancelable) {
        hostActivity.showLoading(cancelable);
    }

    public void showLoading(int stringId, boolean cancelable) {
        hostActivity.showLoading(stringId, cancelable);
    }

    public void showLoading(String text, boolean cancelable) {
        hostActivity.showLoading(text, cancelable);
    }

    public void setLoaded() {
        hostActivity.setLoaded();
    }

    public void setLoaded(String text) {
        hostActivity.setLoaded(text);
    }

    public void setLoadingText(String text) {
        hostActivity.setLoadingText(text);
    }

    public void setLoadingText(int resId) {
        hostActivity.setLoadingText(resId);
    }

    public void dismissLoading() {
        hostActivity.dismissLoading();
    }

    public void dismissLoadingDelayed(long millis) {
        hostActivity.dismissLoadingDelayed(millis);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 便利方法
    ///////////////////////////////////////////////////////////////////////////

    public void postInMain(Runnable r) {
        hostActivity.postInMain(r);
    }

    public void postInMainDelayed(Runnable r, long delayMillis) {
        hostActivity.postInMainDelayed(r, delayMillis);
    }

    /**
     * 隐藏软键盘 <p/>
     *
     * @return 是否有执行隐藏软键盘的操作
     */
    public boolean hideSoftInput() {
        return hostActivity.hideSoftKeyboard();
    }

    /**
     * 展示软键盘
     * <p/>
     * * @return 是否有执行展示软键盘的操作
     */
    public boolean showSoftKeyboard(EditText target) {
        return hostActivity.showSoftKeyboard(target);
    }

    /**
     * 展示软键盘, 并且光标移到最后面
     * * @return 是否有执行展示软键盘的操作
     */
    public boolean showSoftKeyboardAndMoveToEnd(EditText target) {
        return hostActivity.showSoftKeyboardAndMoveToEnd(target);
    }

    ///////////////////////////////////////////////////////////////////////////
    // RX的便利方法
    ///////////////////////////////////////////////////////////////////////////

    private Scheduler io() {
        return Schedulers.io();
    }

    private Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

}
