package skean.me.base;

import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import skean.me.base.component.AppService;
import skean.me.base.component.BaseActivity;
import skean.me.base.utils.ProgressObservable;
import skean.me.base.widget.DateTimePickerDialog;
import skean.me.base.widget.LoadingDialog2;
import skean.yzsm.com.framework.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.txvSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Boolean> ob = Observable.just(1000).subscribeOn(Schedulers.io()).map(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        try {
                            Thread.sleep(integer);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                });
                ProgressObservable.fromObservable(ob, getContext())
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(new Action1<Boolean>() {
                                      @Override
                                      public void call(Boolean aBoolean) {
                                        toast("完成了");
                                      }
                                  });
            }
        });
    }

}