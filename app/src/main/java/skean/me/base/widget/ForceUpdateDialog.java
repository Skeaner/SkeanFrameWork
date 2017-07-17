package skean.me.base.widget;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.internal.MDButton;
import com.blankj.utilcode.util.LogUtils;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import skean.me.base.component.AppService;
import skean.me.base.component.BaseActivity;
import skean.me.base.component.IntentKey;
import skean.me.base.net.CommonService;
import skean.me.base.net.ProgressInterceptor;
import skean.me.base.utils.FileUtil;
import skean.me.base.utils.NetworkUtil;
import skean.yzsm.com.framework.R;

public class ForceUpdateDialog extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_CHANGELOG = "changeLog";
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_VERSION = "version";
    public static final String EXTRA_FORCE = "force";

    public static final int REQUEST_INSTALL = 99;

    private MDButton btnPositive;
    private MDButton btnNegative;
    private MDButton btnCenter;
    private TextView tvContent;
    private TextView txvForce;
    private View panelInfo;
    private NumberProgressBar pgbProgress;

    private String changeLog;
    private String url;
    private String version;
    private boolean force;

    private Call<ResponseBody> downloadCall;
    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_force_update);
        getExtra();
        setFinishOnTouchOutside(false);
        setTitle(getString(R.string.findNewVersion, version));
        btnPositive = (MDButton) findViewById(R.id.btnPositive);
        btnNegative = (MDButton) findViewById(R.id.btnNegative);
        btnCenter = (MDButton) findViewById(R.id.btnCenter);
        txvForce = (TextView) findViewById(R.id.txvForce);
        panelInfo = findViewById(R.id.panelInfo);
        tvContent = (TextView) findViewById(R.id.txvContent);
        pgbProgress = (NumberProgressBar) findViewById(R.id.pgbProgress);
        btnPositive.setOnClickListener(this);
        btnNegative.setOnClickListener(this);
        btnCenter.setOnClickListener(this);
        tvContent.setText(getString(R.string.changeLog, changeLog));
        if (force) txvForce.setVisibility(View.VISIBLE);
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
        if (requestCode == REQUEST_INSTALL) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
                                                                                          R.style.Theme_AppCompat_Light_Dialog_Alert));
            builder.setTitle(R.string.tips).setMessage("请点击进行应用更新!").setPositiveButton("更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    installApp();
                }
            }).setNegativeButton("暂不", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    btnCenter.performClick();
                }
            }).setCancelable(false).show();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // DELE
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View v) {
        if (v == btnPositive) {
            if (!force) {
                AppService.startDownloadApp(this, url);
                finish();
            } else {
                pgbProgress.setProgress(0);
                panelInfo.setVisibility(View.GONE);
                btnCenter.setVisibility(View.VISIBLE);
                btnPositive.setVisibility(View.GONE);
                btnNegative.setVisibility(View.GONE);
                startDownload();
            }
        } else if (v == btnNegative) {
            if (force) sendLocalBroadcast(new Intent(IntentKey.ACTION_FORCE_UPDATE_EXIT));
            else finish();
        } else if (v == btnCenter) {
            LogUtils.i("isExecuted", downloadCall.isExecuted());
            LogUtils.i("isCanceled", downloadCall.isCanceled());
            if (downloadCall != null && downloadCall.isExecuted() && !downloadCall.isCanceled()) {
                downloadCall.cancel();
            }
            if (force) sendLocalBroadcast(new Intent(IntentKey.ACTION_FORCE_UPDATE_EXIT));
            else finish();
        }
    }

    private Callback<ResponseBody> downloadResponse = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                FileUtil.storeFile(tempFile, response.body().byteStream());
                installApp();
            } catch (IOException e) {
                e.printStackTrace();
                onFailure(call, e);
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ForceUpdateDialog.this,
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
            } else {
                pgbProgress.setProgress(100);
            }
        }
    };

    private File createTempApk() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.noSdcardMountedDownloadFail, Toast.LENGTH_SHORT).show();
            return null;
        }
        File apkFile = new File(Environment.getExternalStorageDirectory(), "boaishu.apk");
        boolean created = true;
        if (!apkFile.exists()) {
            try {
                created = apkFile.createNewFile();
            } catch (IOException e) {
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
                                                      .setDataAndType(Uri.fromFile(tempFile), AppService.APK_MIME_TYPE);
        startActivityForResult(intent, REQUEST_INSTALL);
    }

}
