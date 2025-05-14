package me.skean.skeanframework.component;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import me.skean.skeanframework.R;
import me.skean.skeanframework.utils.AMapUtil;
import skean.yzsm.com.easypermissiondialog.EasyPermissionDialog;

/**
 * 地图预览
 */
public class MapActivity extends BaseActivity implements AMapLocationListener, LocationSource {

    private static final String TAG = "MapActivity";

    public static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    public static final int REQUEST_SETTING = 1;

    public static final String EXTRA_SHOW_LOCATION_ONLY = "show_location_only";
    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_LONGITUDE = "longitude";

    private MapView mapView;

    private AMap map;
    private AMapLocationClient client;
    private OnLocationChangedListener myLcListener;

    private boolean showLocationOnly = false;
    private boolean isLocated = false;

    private String address = null;
    private double latitude = 0;
    private double longitude = 0;

    ///////////////////////////////////////////////////////////////////////////
    // LC
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sfw_activity_map);
        showLocationOnly = getIntent().getBooleanExtra(EXTRA_SHOW_LOCATION_ONLY, false);
        latitude = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0);
        longitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0);
        address = getIntent().getStringExtra(EXTRA_ADDRESS);
        initView();
        mapView.onCreate(savedInstanceState);
        if (map == null) map = mapView.getMap();
        if (!showLocationOnly) {
            map.moveCamera(CameraUpdateFactory.zoomTo(map.getMaxZoomLevel() - 2));
            tryLocate();
        }
        else showLocation();
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.mapView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (client != null) client.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!showLocationOnly) getMenuInflater().inflate(R.menu.sfw_menu_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.miConfirm) {
            if (!isLocated) {
                ToastUtils.showShort("定位中, 请稍后...");
            }
            else {
                Intent data = new Intent().putExtra(EXTRA_LATITUDE, latitude)
                                          .putExtra(EXTRA_LONGITUDE, longitude)
                                          .putExtra(EXTRA_ADDRESS, address);
                setResult(RESULT_OK, data);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING) {
            if (PermissionUtils.isGranted(PERMISSIONS)) tryLocate();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // DELE
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onLocationChanged(AMapLocation lc) {
        if (lc != null && lc.getErrorCode() == 0) {
            isLocated = true;
            latitude = lc.getLatitude();
            longitude = lc.getLongitude();
            address = lc.getAddress();
            LogUtils.iTag(TAG, "定位成功", "纬度", latitude, "经度", longitude, "地址", address);
            myLcListener.onLocationChanged(lc);
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        myLcListener = onLocationChangedListener;
        if (client == null) {
            client = AMapUtil.newLocationClient(getContext(), AMapUtil.LType.MIXED, false, this);
            client.startLocation();
        }
    }

    @Override
    public void deactivate() {
        if (client != null) client.stopLocation();
        client = null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // CTRL
    ///////////////////////////////////////////////////////////////////////////

    public void tryLocate() {
        XXPermissions.with(this).permission(PERMISSIONS).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean allGranted) {
                if (allGranted) {
                    startMyLocation();
                }
            }

            @Override
            public void onDenied(List<String> permissions, boolean doNotAskAgain) {
                if (doNotAskAgain) {
                    EasyPermissionDialog.build(getThis()).permissions(PERMISSIONS).typeTemporaryDeny(allow -> {
                        if (allow) tryLocate();
                    }).show();
                }
                else {
                    EasyPermissionDialog.build(getThis()).permissions(PERMISSIONS).typeNeverAsk(null).show();
                }

            }

        });
    }

    private void startMyLocation() {
        // 设置定位监听
        map.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        map.getUiSettings().setMyLocationButtonEnabled(true);
        MyLocationStyle style = new MyLocationStyle();
        style.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.sfw_ic_location_point_red))
             .strokeColor(Color.TRANSPARENT);
        map.setMyLocationStyle(style);
        map.setMyLocationEnabled(true);
    }

    private void showLocation() {
        LatLng latLng = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(latLng).title("地点").snippet(address).draggable(false));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, map.getMaxZoomLevel() - 2));
    }

}
