package skean.me.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.androidannotations.api.sharedpreferences.EditorHelperExt;

import skean.me.base.component.BaseActivity;
import skean.yzsm.com.framework.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.txvSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matisse.from(MainActivity.this)
                       .choose(MimeType.of(MimeType.JPEG, MimeType.PNG), true)
                       .showSingleMediaType(true)
                       .capture(true)
                       .captureStrategy(new CaptureStrategy(true, "skean.yzsm.com.framework.fileprovider"))
                       .countable(true)
                       .maxSelectable(1)
                       .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                       .thumbnailScale(0.85f)
                       .imageEngine(new PicassoEngine())
                       .forResult(9);

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}