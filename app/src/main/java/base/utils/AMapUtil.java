package base.utils;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.model.LatLng;

import androidx.annotation.IntDef;

import static base.utils.AMapUtil.LType.GPS;
import static base.utils.AMapUtil.LType.MIXED;
import static base.utils.AMapUtil.LType.NETWORK;

/**
 * 高德地图定位工具
 */
public class AMapUtil {

    @IntDef({NETWORK, GPS, MIXED})
    public @interface LType {
        int NETWORK = 1;
        int GPS = 2;
        int MIXED = 4;
    }

    /**
     * 默认搜索间隔, 循环定位的定位器
     */
    public static AMapLocationClient newLocationClient(Context context, @LType int type, AMapLocationListener listener) {
        AMapLocationClient client = new AMapLocationClient(context);
        client.setLocationOption(newOption(type, 0, false));
        if (listener != null) client.setLocationListener(listener);
        return client;
    }

    /**
     * 指定搜索间隔, 循环定位的定位器
     */
    public static AMapLocationClient newLocationClient(Context context, @LType int type, int scanSpan, AMapLocationListener listener) {
        AMapLocationClient client = new AMapLocationClient(context);
        client.setLocationOption(newOption(type, scanSpan, false));
        if (listener != null) client.setLocationListener(listener);
        return client;
    }

    /**
     * 默认搜索间隔, 单次定位的定位器
     */
    public static AMapLocationClient newLocationClient(Context context, @LType int type, boolean once, AMapLocationListener listener) {
        AMapLocationClient client = new AMapLocationClient(context);
        client.setLocationOption(newOption(type, 0, once));
        if (listener != null) client.setLocationListener(listener);
        return client;
    }

    private static AMapLocationClientOption newOption(@LType int type, int scanSpan, boolean once) {
        AMapLocationClientOption op = new AMapLocationClientOption();
        switch (type) {
            case LType.GPS:
                op.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
                if (scanSpan == 0) scanSpan = 3000;
                op.setInterval(scanSpan);
                op.setOnceLocation(once);
                op.setNeedAddress(false);
                break;
            case LType.MIXED:
                op.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                if (scanSpan == 0) scanSpan = 3000;
                op.setInterval(scanSpan);
                op.setOnceLocation(once);
                op.setNeedAddress(true);
                op.setWifiActiveScan(true);
                break;
            case LType.NETWORK:
                op.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
                if (scanSpan == 0) scanSpan = 6000;
                op.setInterval(scanSpan);
                op.setOnceLocation(once);
                op.setNeedAddress(true);
                op.setWifiActiveScan(true);
                break;
        }
        return op;
    }

    /**
     * 生成在线预览的地图图片URL
     *
     * @param latitude  纬度(GCJ坐标系)
     * @param longitude 经度(GCJ坐标系)
     * @return 生成的URL
     */
    public static String aMapLocationMapURL(String latitude, String longitude) {
        return aMapLocationMapURL(Double.valueOf(latitude), Double.valueOf(longitude));
    }

    /**
     * 生成高德地图在线预览的地图图片URL
     *
     * @param latitude  纬度(GCJ坐标系)
     * @param longitude 经度(GCJ坐标系)
     * @return 生成的URL
     */
    public static String aMapLocationMapURL(double latitude, double longitude) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://restapi.amap.com/v3/staticmap?")
          .append("key=")
          .append("4700e3c0613c00e3d9020ffd6a7c1328") //这个授权码专门用来在线预览...
          .append("&location=")
          .append(longitude)//经度先传 ,下同
          .append(",")
          .append(latitude) //纬度
          .append("&size=")
          .append(400)
          .append("*")
          .append(200)
          .append("&zoom=")
          .append(16)
          .append("&markers=")
          .append("mid,,:")
          .append(longitude)//经度
          .append(",")
          .append(latitude); //纬度
        return sb.toString();
    }

    /**
     * 生成在线预览的地图图片URL
     *
     * @param latitude  纬度(GCJ坐标系)
     * @param longitude 经度(GCJ坐标系)
     * @return 生成的URL
     */
    public static String gcjLocationMapURL(String latitude, String longitude) {
        return gcjLocationMapURL(Double.valueOf(latitude), Double.valueOf(longitude));
    }

    /**
     * 生成在线预览的地图图片URL
     *
     * @param latitude  纬度(GCJ坐标系)
     * @param longitude 经度(GCJ坐标系)
     * @return 生成的URL
     */
    public static String gcjLocationMapURL(double latitude, double longitude) {
        double[] latlon = gcjToBaiduCoordinate(latitude, longitude);
        return bdLocationMapURL(latlon[0], latlon[1]);
    }

    /**
     * 生成在线预览的地图图片URL
     *
     * @param latitude  纬度(BD坐标系)
     * @param longitude 经度(BD坐标系)
     * @return 生成的URL
     */
    public static String bdLocationMapURL(String latitude, String longitude) {
        return bdLocationMapURL(Double.valueOf(latitude), Double.valueOf(longitude));
    }

    /**
     * 生成在线预览的地图图片URL
     *
     * @param latitude  纬度(BD坐标系)
     * @param longitude 经度(BD坐标系)
     * @return 生成的URL
     */
    public static String bdLocationMapURL(double latitude, double longitude) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://api.map.baidu.com/staticimage/v2?")
          .append("ak=")
          .append("pt2W3vyG90CG0npRS2b02zpErAxnG7xI") //这个授权码专门用来在线预览...
          .append("&mcode=") //假的...
          .append("00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:19:87:01:28;me.skean.mappreview")
          .append("&center=")
          .append(longitude)//经度先传 ,下同
          .append(",")
          .append(latitude) //纬度
          .append("&width=")
          .append(300)
          .append("&height=")
          .append(200)
          .append("&zoom=")
          .append(16)
          .append("&markers=")
          .append(longitude)//经度
          .append(",")
          .append(latitude) //纬度
          .append("&copyright=")
          .append(1);
        return sb.toString();
    }

    /**
     * 将国标系转换为百度坐标系
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return 转换后的坐标系 ,第一个是纬度 ,第二个是经度
     */
    public static double[] gcjToBaiduCoordinate(String latitude, String longitude) {
        return gcjToBaiduCoordinate(Double.valueOf(latitude), Double.valueOf(longitude));

    }

    /**
     * 将国标系转换为百度坐标系
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return 转换后的坐标系 ,第一个是纬度 ,第二个是经度
     */
    public static double[] gcjToBaiduCoordinate(double latitude, double longitude) {
        LatLng sourceLatLng = new LatLng(latitude, longitude);
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.BAIDU);
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return new double[]{desLatLng.latitude, desLatLng.longitude};
    }

    /**
     * 将百度坐标系为国标系转换
     *
     * @return 转换后的坐标系 ,第一个是纬度 ,第二个是经度
     */
    public static double[] baiduToGcjCoordinate(String latitude, String longitude) {
        return baiduToGcjCoordinate(Double.valueOf(latitude), Double.valueOf(longitude));

    }

    /**
     * 将百度坐标系为国标系转换
     *
     * @return 转换后的坐标系 ,第一个是纬度 ,第二个是经度
     */
    public static double[] baiduToGcjCoordinate(double latitude, double longitude) {
        double x_pi = 3.141592653589793 * 3000.0 / 180.0;
        double z = Math.sqrt(longitude * longitude + latitude * latitude) + 0.00002 * Math.sin(latitude * x_pi);
        double theta = Math.atan2(latitude, longitude) + 0.000003 * Math.cos(longitude * x_pi);
        return new double[]{z * Math.sin(theta) + 0.006, z * Math.cos(theta) + 0.0065};
    }

    /**
     * 将国标系转换为GPS坐标系
     * @return 转换后的坐标系 ,第一个是纬度 ,第二个是经度
     */
    public static double[] gcjToWgsCoordinate(String lat, String lon) {
        return gcjToWgsCoordinate(Double.valueOf(lat), Double.valueOf(lon));
    }

    /**
     * 将国标系转换为GPS坐标系
     * @return 转换后的坐标系 ,第一个是纬度 ,第二个是经度
     */
    public static double[] gcjToWgsCoordinate(double lat, double lon) {
        double pi = 3.1415926535897932384626;
        double a = 6378245.0;
        double ee = 0.00669342162296594323;
        double dLat = transformLat(lon - 105.0, lat - 35.0, pi);
        double dLon = transformLon(lon - 105.0, lat - 35.0, pi);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new double[]{lat * 2 - mgLat, lon * 2 - mgLon};
    }

    private static double transformLat(double x, double y, double pi) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y, double pi) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

}
