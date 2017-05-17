package skean.me.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import skean.me.base.component.AppService;
import skean.me.base.component.BaseActivity;
import skean.me.base.widget.DateTimePickerDialog;
import skean.yzsm.com.framework.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppService.startCheckUpdateInPGYER(getContext(), false);
        findViewById(R.id.txvSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialog dialog = new DateTimePickerDialog(MainActivity.this, false);
                dialog.setMinDate(System.currentTimeMillis())
                      .setMaxDate(System.currentTimeMillis())
                      .setSelectedDate(System.currentTimeMillis())
                      .setUse24Hour(true)
                      .setCallback(new DateTimePickerDialog.Callback() {
                          @Override
                          public void onDateTimeSet(Calendar ca, Date date, Long millis, String text) {
                              Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                          }

                          @Override
                          public void onCancelled() {

                          }
                      })
                      .show();
            }
        });
    }

}