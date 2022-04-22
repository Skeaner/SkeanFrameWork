package me.skean.skeanframework.component;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.skean.skeanframework.utils.BetterActivityResult;
import me.skean.skeanframework.widget.LoadingDialog;

/**
 * App的DialogFragment基类 <p/>
 */
@SuppressWarnings("unused")
public abstract class BaseFragment extends Fragment {
    protected Bundle savedInstanceStateCache;
    protected BaseActivity hostActivity;
    private Context context;
    protected LoadingDialog loadingDialog;

    protected boolean isMenuCreated;

    private final Set<Integer> msgWhats = new HashSet<>();
    private final Set<String> msgTokens = new HashSet<>();

    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);


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
    public void onDestroyView() {
        for (Integer msgWhat : msgWhats) {
            removeMainMessages(msgWhat);
        }
        msgWhats.clear();
        for (String msgToken : msgTokens) {
            removeMainCallbacksAndMessages(msgToken);
        }
        msgTokens.clear();
        super.onDestroyView();
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

    public boolean addMainHandlerCallBack(Handler.Callback callback) {
        return hostActivity.addMainHandlerCallBack(callback);
    }

    public boolean removeMainHandlerCallBack(Handler.Callback callback) {
        return hostActivity.removeMainHandlerCallBack(callback);
    }

    public void sendMessage(int what, Object object) {
        msgWhats.add(what);
        hostActivity.sendMessage(what, object);
    }

    public void sendMessageDelayed(long delayMills, int what, Object object) {
        msgWhats.add(what);
        hostActivity.sendMessageDelayed(delayMills, what, object);
    }

    public void postInMain(Runnable r) {
        msgTokens.add(UUID.randomUUID().toString());
        hostActivity.postInMain(r);
    }

    public void postInMainDelayed(long delayMillis, Runnable r) {
        msgTokens.add(UUID.randomUUID().toString());
        hostActivity.postInMainDelayed(delayMillis, r);
    }

    public void postInMainDelayed(long millis, String token, Runnable r) {
        msgTokens.add(token);
        hostActivity.postInMainDelayed(millis, token, r);
    }

    public void removeMainCallbacksAndMessages(String token) {
        hostActivity.removeMainCallbacksAndMessages(token);
    }

    public void removeMainMessages(int what) {
        hostActivity.removeMainMessages(what);
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

    protected Scheduler io() {
        return Schedulers.io();
    }

    protected Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

}
