package skean.me.base.widget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import skean.me.base.component.AppService;
import skean.yzsm.com.hzevent.R;

public class UpdateDialog extends Activity implements View.OnClickListener {
    Button btnPositive;
    Button btnNegative;
    TextView tvContent;

    String changeLog;
    String url;
    String version;
    public static final String EXTRA_CHANGELOG = "changeLog";
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_VERSION = "version";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update);
        getExtra();
        setFinishOnTouchOutside(true);
        setTitle(getString(R.string.findNewVersion, version));
        btnPositive = (Button) findViewById(R.id.btnPositive);
        btnNegative = (Button) findViewById(R.id.btnNegative);
        btnPositive.setText(R.string.update);
        btnNegative.setText(R.string.later);
        tvContent = (TextView) findViewById(R.id.txvContent);
        btnPositive.setOnClickListener(this);
        btnNegative.setOnClickListener(this);
        tvContent.setText(getString(R.string.changeLog, changeLog));
    }

    protected void getExtra() {
        changeLog = getIntent().getStringExtra(EXTRA_CHANGELOG);
        url = getIntent().getStringExtra(EXTRA_URL);
        version = getIntent().getStringExtra(EXTRA_VERSION);
    }

    @Override
    public void onClick(View v) {
        if (v == btnPositive) AppService.startDownloadApp(this, url);
        finish();
    }
}
