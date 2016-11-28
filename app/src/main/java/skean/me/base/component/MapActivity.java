package skean.me.base.component;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import skean.me.base.utils.BDMapUtil;
import skean.yzsm.com.framework.R;

/**
 * 地图预览
 */
@EActivity(R.layout.activity_map)
@OptionsMenu(R.menu.map_context)
public class MapActivity extends BasePictureActivity implements MKOfflineMapListener {
    @ViewById
    MapView mapView;
    @OptionsMenuItem
    MenuItem mniCapture;

    BaiduMap baiduMap;
    @Extra
    double latitude;
    @Extra
    double longitude;
    @Extra
    boolean canCapture;
    @Extra
    long targetId;
    @Extra
    boolean isBaiduLatlon;

    MKOfflineMap offlineMap;
    int cityId = CITYID_HEZHOU;
    boolean hasLocation;

    @SystemService
    NotificationManager noticeManager;
    int noticeId = 2;

    public static final int CITYID_HEZHOU = 260;
    public static final int CITYID_ZHONGSHAN = 187;

    private static final String TAG = "MapActivity";


    @Override
    protected long containerId() {
        return 0;
    }

    @AfterViews
    protected void init() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        offlineMap = new MKOfflineMap();
        offlineMap.init(this);
        baiduMap = mapView.getMap();
        hasLocation = latitude != 0 && longitude != 0;
        if (hasLocation) toMapPoint(latitude, longitude);
        containerId = targetId;
//        initOfflineMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        if (canCapture) mniCapture.setVisible(true);
        return result;
    }

    @OptionsItem(android.R.id.home)
    protected void homeClick() {
        finish();
    }

    @OptionsItem(R.id.mniCapture)
    protected void mniCaptureClick() {
        new AlertDialog.Builder(context).setTitle(R.string.tips)
                                        .setMessage("截取当前地图图片并返回?")
                                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                baiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
                                                    @Override
                                                    public void onSnapshotReady(Bitmap bitmap) {
                                                        showLoading(false);
                                                        File mapFile = new File(AppApplication.getAppPicturesDirectory(), "temp.jpg");
                                                        OutputStream os;
                                                        if (mapFile.exists()) mapFile.delete();
                                                        try {
                                                            mapFile.createNewFile();
                                                            os = new FileOutputStream(mapFile);
                                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                                                            os.close();
                                                            returnPictures(mapFile);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            setResult(RESULT_ERROR);
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, null)
                                        .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mapView.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        offlineMap.destroy();
        noticeManager.cancel(noticeId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    protected void initOfflineMap() {
        MKOLUpdateElement info = offlineMap.getUpdateInfo(cityId);
        if (info == null || info.update) {
            new AlertDialog.Builder(context).setTitle(R.string.tips)
                                            .setMessage("检测到离线地图有数据更新, 是否进行下载更新? (注意: 离线地图包体积较大, 建议在wifi网络下载)")
                                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    offlineMap.start(cityId);
                                                    showNotice(0);
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, null)
                                            .show();
        } else if (!hasLocation) {
            toMapPoint(info.geoPt.latitude, info.geoPt.longitude);
        }
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = offlineMap.getUpdateInfo(state);
                // 处理下载进度更新提示
                if (update != null) {
                    showNotice(update.ratio);
                    if (update.ratio == 100 && !hasLocation) toMapPoint(update.geoPt.latitude, update.geoPt.longitude);
                }
            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Toast.makeText(context, "安装离线地图成功", Toast.LENGTH_SHORT).show();
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                break;
            default:
                break;
        }
    }

    protected void showNotice(int progress) {
        noticeManager.notify(noticeId,
                             new Notification.Builder(this).setContentTitle("下载离线地图")
                                                           .setContentText(String.format("正在下载%d%%", progress))
                                                           .setSmallIcon(R.drawable.ic_launcher)
                                                           .setAutoCancel(progress == 100)
                                                           .setContentIntent(PendingIntent.getActivity(context,
                                                                                                       0,
                                                                                                       new Intent(),
                                                                                                       PendingIntent.FLAG_UPDATE_CURRENT))
                                                           .setProgress(100, progress, false)
                                                           .getNotification());
    }

    protected void toMapPoint(double lat, double lon) {
        double[] latlon;
        if (isBaiduLatlon) latlon = new double[]{lat, lon};
        else latlon = BDMapUtil.convertToBaiduCoordType(lat, lon);
        LatLng desLatLng = new LatLng(latlon[0], latlon[1]);
        if (hasLocation) {
            OverlayOptions options = new MarkerOptions().position(desLatLng)
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_point_red));
            ;
            baiduMap.addOverlay(options);
        }
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(desLatLng, 18));
    }


}
