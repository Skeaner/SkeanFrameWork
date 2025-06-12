package me.skean.skeanframework.component;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.trello.rxlifecycle4.LifecycleProvider;
import com.trello.rxlifecycle4.LifecycleTransformer;
import com.trello.rxlifecycle4.RxLifecycle;
import com.trello.rxlifecycle4.android.FragmentEvent;
import com.trello.rxlifecycle4.android.RxLifecycleAndroid;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.CallSuper;
import androidx.annotation.CheckResult;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwnerKt;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import kotlin.jvm.functions.Function0;
import me.skean.skeanframework.R;
import me.skean.skeanframework.component.function.LoadingDialog;
import me.skean.skeanframework.component.function.LoadingDialog3;
import me.skean.skeanframework.rx.DefaultSingleObserver;

/**
 * App的DialogFragment基类 <p/>
 */
@SuppressWarnings("unused")
public abstract class BaseFragment extends Fragment implements LifecycleProvider<FragmentEvent> {
    public Bundle savedInstanceStateCache;
    protected BaseActivity hostActivity;
    private Context context;
    protected LoadingDialog3 loadingDialog;

    protected boolean isMenuCreated;

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    ///////////////////////////////////////////////////////////////////////////
    // 设置/生命周期/初始化
    ///////////////////////////////////////////////////////////////////////////

    public BaseFragment() {
        super();
    }

    public BaseFragment(@LayoutRes int layoutId) {
        super(layoutId);
    }

    public float getFragmentIndex() {
        throw new RuntimeException("使用前必须要复写该方法");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        savedInstanceStateCache = savedInstanceState;
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
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
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
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
        LoadingDialog3 dialog3 = getLoadingDialog(text, cancelable);
        dialog3.setFinished(false, false);
        if (!dialog3.isShowing()) {
            dialog3.show(getChildFragmentManager());
        }
    }

    public void showLoaded(boolean success) {
        showLoaded(success, "");
    }

    public void showLoaded(boolean success, String text) {
        showLoaded(success, text, true);
    }

    public void showLoaded(boolean success, String text, boolean cancelable) {
        LoadingDialog3 dialog3 = getLoadingDialog(text, cancelable);
        dialog3.setFinished(true, success);
        if (!dialog3.isShowing()) {
            dialog3.show(getChildFragmentManager());
        }
    }

    public void setLoadingText(String text) {
        getLoadingDialog().setMessage(text);
    }

    public void setLoadingText(int resId) {
        getLoadingDialog().setMessage(getString(resId));
    }

    public void dismissLoading() {
        LoadingDialog3 dialog3 = getLoadingDialog();
        if (dialog3.isShowing()) getLoadingDialog().dismiss();
    }

    public void dismissLoadingDelayed(long millis) {
        Single.timer(millis, TimeUnit.MILLISECONDS)
              .observeOn(mainThread())
              .compose(bindToLifecycle())
              .subscribe(new DefaultSingleObserver<>() {
                  @Override
                  public void onSuccess2(Long aLong) {
                      dismissLoading();
                  }
              });
    }

    private LoadingDialog3 getLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = getLoadingDialog(getString(R.string.loading), true);
        }
        return loadingDialog;
    }

    private LoadingDialog3 getLoadingDialog(String text, boolean cancelable) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog3();
            loadingDialog.setMessage(text);
            loadingDialog.setCancelable(cancelable);
        }
        else {
            loadingDialog.setMessage(text);
            loadingDialog.setCancelable(cancelable);
        }
        return loadingDialog;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 便利方法
    ///////////////////////////////////////////////////////////////////////////

    public Disposable postInMain(Function0<Void> r) {
        return Single.just(1L).observeOn(mainThread()).compose(bindToLifecycle()).subscribe((aLong, e) -> {
            if (e != null) ToastUtils.showShort(e.getMessage());
            else r.invoke();
        });
    }

    public Disposable postInMainDelayed(long delayMills, Function0<Void> r) {
        return Single.timer(delayMills, TimeUnit.MILLISECONDS)
                     .observeOn(mainThread())
                     .compose(bindToLifecycle())
                     .subscribe((aLong, e) -> {
                         if (e != null) ToastUtils.showShort(e.getMessage());
                         else r.invoke();
                     });
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
