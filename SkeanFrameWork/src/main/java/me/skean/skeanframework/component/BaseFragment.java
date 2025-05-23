package me.skean.skeanframework.component;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.trello.rxlifecycle4.LifecycleProvider;
import com.trello.rxlifecycle4.LifecycleTransformer;
import com.trello.rxlifecycle4.RxLifecycle;
import com.trello.rxlifecycle4.android.FragmentEvent;
import com.trello.rxlifecycle4.android.RxLifecycleAndroid;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.CallSuper;
import androidx.annotation.CheckResult;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import kotlin.jvm.functions.Function0;
import me.skean.skeanframework.component.function.LoadingDialog;

/**
 * App的DialogFragment基类 <p/>
 */
@SuppressWarnings("unused")
public abstract class BaseFragment extends Fragment implements LifecycleProvider<FragmentEvent> {
    protected Bundle savedInstanceStateCache;
    protected BaseActivity hostActivity;
    private Context context;
    protected LoadingDialog loadingDialog;

    protected boolean isMenuCreated;

    private final Set<Integer> msgWhats = new HashSet<>();
    private final Set<String> msgTokens = new HashSet<>();

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    ///////////////////////////////////////////////////////////////////////////
    // 设置/生命周期/初始化
    ///////////////////////////////////////////////////////////////////////////

    public BaseFragment() {
        super();
    }

    public BaseFragment(@LayoutRes int layoutId){
        super(layoutId);
    }

    public float getFragmentIndex() {
        throw new RuntimeException("使用前必须要复写该方法");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
        savedInstanceStateCache = savedInstanceState;
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    @CallSuper
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    @CallSuper
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override
    public void onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
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
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
        this.hostActivity = (BaseActivity) activity;
    }

    @Override
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        isMenuCreated = true;
    }

    public void setOnBackPressedListener(Function0<Boolean> onBackPressedListener) {
        hostActivity.setOnBackPressedListener(onBackPressedListener);
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


    ///////////////////////////////////////////////////////////////////////////
    // RX的便利方法
    ///////////////////////////////////////////////////////////////////////////

    protected Scheduler io() {
        return Schedulers.io();
    }

    protected Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    ///////////////////////////////////////////////////////////////////////////
    // RXLife
    ///////////////////////////////////////////////////////////////////////////

    @Override
    @NonNull
    @CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }
}
