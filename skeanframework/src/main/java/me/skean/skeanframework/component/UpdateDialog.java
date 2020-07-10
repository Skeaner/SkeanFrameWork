package me.skean.skeanframework.component;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.qmuiteam.qmui.widget.QMUIProgressBar;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import me.skean.skeanframework.BuildConfig;
import me.skean.skeanframework.R;
import me.skean.skeanframework.event.ForceUpdateExitEvent;
import me.skean.skeanframework.net.CommonService;
import me.skean.skeanframework.net.ProgressInterceptor;
import me.skean.skeanframework.utils.NetworkUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateDialog extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_CHANGELOG = "changeLog";
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_VERSION = "version";
    public static final String EXTRA_FORCE = "force";

    public static final int REQUEST_INSTALL = 99;

    private Button btnPositive;
    private Button btnNegative;
    private Button btnCenter;
    private TextView tvContent;
    private TextView txvForce;
    private View panelInfo;
    private View panelProgress;
    private QMUIProgressBar pgbProgress;

    private String changeLog;
    private String url;
    private String version;
    private boolean force;

    private Call<ResponseBody> downloadCall;
    private File tempFile;

    public static void show(Context c, String version, String changeLog, String downloadUrl, boolean isForceUpdate) {
        c.startActivity(new Intent(c, UpdateDialog.class).putExtra(UpdateDialog.EXTRA_VERSION, version)
                                                         .putExtra(UpdateDialog.EXTRA_CHANGELOG, changeLog)
                                                         .putExtra(UpdateDialog.EXTRA_URL, downloadUrl)
                                                         .putExtra(UpdateDialog.EXTRA_FORCE, isForceUpdate)
                                                         .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update);
        getExtra();
        setFinishOnTouchOutside(false);
        setTitle(getString(R.string.findNewVersion, version));
        btnPositive = findViewById(R.id.btnPositive);
        btnNegative = findViewById(R.id.btnNegative);
        btnCenter = findViewById(R.id.btnCenter);
        txvForce = findViewById(R.id.txvForce);
        panelInfo = findViewById(R.id.panelInfo);
        panelProgress = findViewById(R.id.panelProgress);
        tvContent = findViewById(R.id.txvContent);
        pgbProgress = findViewById(R.id.pgbProgress);
        btnPositive.setOnClickListener(this);
        btnNegative.setOnClickListener(this);
        btnCenter.setOnClickListener(this);
        tvContent.setText(getString(R.string.changeLog, changeLog));
        if (force) txvForce.setVisibility(View.VISIBLE);
        pgbProgress.setQMUIProgressBarTextGenerator((progressBar, value, maxValue) -> 100 * value / maxValue + "%");
    }

    protected void getExtra() {
        changeLog = getIntent().getStringExtra(EXTRA_CHANGELOG);
        url = getIntent().getStringExtra(EXTRA_URL);
        version = getIntent().getStringExtra(EXTRA_VERSION);
        force = getIntent().getBooleanExtra(EXTRA_FORCE, false);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INSTALL) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
                                                                                          R.style.Theme_AppCompat_Light_Dialog_Alert));
            builder.setTitle(R.string.tips)
                   .setMessage("请点击进行应用更新!")
                   .setPositiveButton("更新", (dialog, which) -> installApp())
                   .setNegativeButton("暂不", (dialog, which) -> btnCenter.performClick())
                   .setCancelable(false)
                   .show();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // DELE
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View v) {
        if (v == btnPositive) {
            pgbProgress.setProgress(0);
            panelInfo.setVisibility(View.GONE);
            panelProgress.setVisibility(View.VISIBLE);
            btnCenter.setVisibility(View.VISIBLE);
            btnPositive.setVisibility(View.GONE);
            btnNegative.setVisibility(View.GONE);
            startDownload();
        }
        else if (v == btnNegative) {
            if (force) {
                EventBus.getDefault().post(new ForceUpdateExitEvent());
            }
            else finish();
        }
        else if (v == btnCenter) {
            LogUtils.i("isExecuted", downloadCall.isExecuted());
            LogUtils.i("isCanceled", downloadCall.isCanceled());
            if (downloadCall != null && downloadCall.isExecuted() && !downloadCall.isCanceled()) {
                downloadCall.cancel();
            }
            if (force) {
                EventBus.getDefault().post(new ForceUpdateExitEvent());
            }
            else finish();
        }
    }

    private Callback<ResponseBody> downloadResponse = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                FileIOUtils.writeFileFromIS(tempFile, response.body().byteStream());
                installApp();
            }
            catch (Exception e) {
                e.printStackTrace();
                onFailure(call, e);
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(UpdateDialog.this,
                                                                                          R.style.Theme_AppCompat_Light_Dialog_Alert));
            builder.setTitle(R.string.tips).setMessage("下载出错, 请尝试重新下载").setPositiveButton("重试", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startDownload();
                }
            }).setNegativeButton("暂不", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    btnCenter.performClick();
                }
            }).setCancelable(false).show();
        }
    };

    private ProgressInterceptor.DownloadListener progressResponse = new ProgressInterceptor.DownloadListener() {
        @Override
        public void downloadProgress(long bytesRead, long contentLength, int percentage, boolean done) {
            if (!done) {
                pgbProgress.setProgress(percentage);
            }
            else {
                pgbProgress.setProgress(100);
            }
        }
    };

    private File createTempApk() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.noSdcardMountedDownloadFail, Toast.LENGTH_SHORT).show();
            return null;
        }
        File apkFile = new File(getExternalCacheDir(), "update.apk");
        boolean created = true;
        if (!apkFile.exists()) {
            try {
                created = apkFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                created = false;
            }
        }
        if (!created) {
            Toast.makeText(this, R.string.createFileFail, Toast.LENGTH_SHORT).show();
            apkFile = null;
        }
        return apkFile;
    }

    private void startDownload() {
        tempFile = createTempApk();
        if (tempFile == null) {
            btnCenter.performClick();
            return;
        }
        if (url == null) {
            Toast.makeText(this, "下载地址出错!", Toast.LENGTH_SHORT).show();
            btnCenter.performClick();
            return;
        }
        downloadCall = NetworkUtil.progressRetrofit(CommonService.BASE_URL, null, progressResponse)
                                  .create(CommonService.class)
                                  .downLoad(url);
        downloadCall.enqueue(downloadResponse);
    }

    private void installApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                      .setDataAndType(FileProvider.getUriForFile(this,
                                                                                                 BuildConfig.APPLICATION_ID + ".fileprovider",
                                                                                                 tempFile),
                                                                      "application/vnd.android.package-archive");
        startActivityForResult(intent, REQUEST_INSTALL);
    }

}
