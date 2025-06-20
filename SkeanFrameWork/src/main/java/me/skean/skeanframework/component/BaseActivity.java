package me.skean.skeanframework.component;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.trello.rxlifecycle4.LifecycleProvider;
import com.trello.rxlifecycle4.LifecycleTransformer;
import com.trello.rxlifecycle4.RxLifecycle;
import com.trello.rxlifecycle4.android.ActivityEvent;
import com.trello.rxlifecycle4.android.RxLifecycleAndroid;

import java.util.concurrent.TimeUnit;

import androidx.annotation.CallSuper;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import kotlin.jvm.functions.Function0;
import me.skean.skeanframework.R;

import me.skean.skeanframework.component.function.LoadingDialog3;
import me.skean.skeanframework.rx.DefaultSingleObserver;

/**
 * App的Activity基类 <p/>
 */
@SuppressWarnings("unused")
@SuppressLint("HandlerLeak")
public class BaseActivity extends AppCompatActivity implements LifecycleProvider<ActivityEvent> {
    public Bundle savedInstanceStateCache;
    protected Context context = null;
    protected ActionBar actionBar;
    protected LoadingDialog3 loadingDialog;

    protected boolean useHomeAsBack = true;
    protected boolean useHomeShowTitle = true;
    protected boolean isMenuCreated = false;

    private Function0<Boolean> onBackPressedListener;

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    ///////////////////////////////////////////////////////////////////////////
    // 声明周期/初始化/设置
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceStateCache = savedInstanceState;
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        context = this;
        initActionBar();
    }

    @Override
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        super.onDestroy();
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    @CallSuper
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        isMenuCreated = true;
        return result;
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar, boolean showBack, boolean showTitle) {
        useHomeShowTitle = showTitle;
        useHomeAsBack = showBack;
        setSupportActionBar(toolbar);
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

    public void setOnBackPressedListener(Function0<Boolean> onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        boolean consumed = false;
        if (onBackPressedListener != null) {
            consumed = onBackPressedListener.invoke();
        }
        if (!consumed) super.onBackPressed();
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
        LoadingDialog3 dialog3 = getLoadingDialog(text, cancelable);
        dialog3.setFinished(false, false);
        if (!dialog3.isShowing()) {
            dialog3.show(getSupportFragmentManager());
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
            dialog3.show(getSupportFragmentManager());
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
    // 状态获取/上下文相关
    ///////////////////////////////////////////////////////////////////////////

    public <T extends Application> T getApp() {
        return (T) getApplication();
    }

    public Context getContext() {
        return context;
    }

    public boolean isMenuCreated() {
        return isMenuCreated;
    }

    public BaseActivity getThis() {
        return this;
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
    // RX的便捷方法
    ///////////////////////////////////////////////////////////////////////////

    protected Scheduler io() {
        return Schedulers.io();
    }

    protected Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    ///////////////////////////////////////////////////////////////////////////
    // RxLife方法
    ///////////////////////////////////////////////////////////////////////////

    @Override
    @NonNull
    @CheckResult
    public final Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }

}
