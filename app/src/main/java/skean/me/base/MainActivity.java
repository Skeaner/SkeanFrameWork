package skean.me.base;

import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

import skean.me.base.component.ImagePagerActivity_;
import skean.me.base.db.AppBaseModel;
import skean.me.base.db.Photo;
import skean.yzsm.com.framework.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.txvSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera");
                ArrayList<Photo> list = new ArrayList<>();
                for (File file : dir.listFiles()) {
                    Photo p = new Photo();
                    p.setDesc("desc:" + file.getName());
                    p.setFile(file);
                    list.add(p);
                }
                ImagePagerActivity_.intent(MainActivity.this).showDescription(true).defaultPosition(3).photoList(list).start();
            }
        });
    }

}