package skean.me.base.component;

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

import androidx.annotation.NonNull;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.PermissionUtils;
import permissions.dispatcher.RuntimePermissions;
import skean.me.base.utils.AMapUtil;
import skean.yzsm.com.easypermissiondialog.EasyPermissionDialog;
import skean.yzsm.com.framework.R;

/**
 * 地图预览
 */
@RuntimePermissions
public class MapActivity extends BaseActivity implements AMapLocationListener, LocationSource {

    private static final String TAG = "MapActivity";

    public static final String LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String[] PERMISSIONS = {LOCATION};
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
        setContentView(R.layout.activity_map);
        showLocationOnly = getIntent().getBooleanExtra(EXTRA_SHOW_LOCATION_ONLY, false);
        latitude = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0);
        longitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0);
        address = getIntent().getStringExtra(EXTRA_ADDRESS);
        initView();
        mapView.onCreate(savedInstanceState);
        if (map == null) map = mapView.getMap();
        if (!showLocationOnly) {
            map.moveCamera(CameraUpdateFactory.zoomTo(map.getMaxZoomLevel() - 2));
            MapActivityPermissionsDispatcher.tryLocateWithPermissionCheck(MapActivity.this);
        } else showLocation();
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
        if (!showLocationOnly) getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mniConfirm) {
            if (!isLocated) toast("定位中, 请稍后...");
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING) {
            if (PermissionUtils.hasSelfPermissions(getContext(), LOCATION)) tryLocate();
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
            LogUtils.i(TAG, "定位成功", "纬度", latitude, "经度", longitude, "地址", address);
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

    @NeedsPermission(LOCATION)
    public void tryLocate() {
        startMyLocation();
    }

    @OnPermissionDenied(LOCATION)
    public void locateDeny() {
        EasyPermissionDialog.build(this).permissions(PERMISSIONS).typeTemporaryDeny(new EasyPermissionDialog.Callback() {
            @Override
            public void onResult(boolean allow) {
                if (allow) MapActivityPermissionsDispatcher.tryLocateWithPermissionCheck(MapActivity.this);
            }

        }).show();
    }

    @OnNeverAskAgain(LOCATION)
    public void locateNever() {
        EasyPermissionDialog.build(this).permissions(PERMISSIONS).typeNeverAsk(REQUEST_SETTING, null).show();
    }

    private void startMyLocation() {
        // 设置定位监听
        map.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        map.getUiSettings().setMyLocationButtonEnabled(true);
        MyLocationStyle style = new MyLocationStyle();
        style.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_point_red)).strokeColor(Color.TRANSPARENT);
        map.setMyLocationStyle(style);
        map.setMyLocationEnabled(true);
    }

    private void showLocation() {
        LatLng latLng = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(latLng).title("地点").snippet(address).draggable(false));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, map.getMaxZoomLevel() - 2));
    }

}
