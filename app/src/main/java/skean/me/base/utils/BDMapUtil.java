package skean.me.base.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

/**
 * 百度定位Builder
 */
public class BDMapUtil {

    public enum Type {
        网络定位, 混合定位, GPS定位
    }

    /**
     * 构架一个默认时间间隔的定位client(网络定位6秒, GPS定位3秒)
     *
     * @param context  上下文
     * @param type     类型
     * @param listener 回调
     * @return 设置好的设置好的LocationClient
     */
    public static LocationClient newLocationClient(Context context, Type type, BDLocationListener listener) {
        LocationClient client = new LocationClient(context, newOption(type, -1));
        if (listener != null) client.registerLocationListener(listener);
        return client;
    }

    /**
     * 构架一个定位client
     *
     * @param context  上下文
     * @param type     定位类型
     * @param scanSpan 定位间隔 ,注意0代表仅定位一次
     * @param listener 定位回调
     * @return 设置好的LocationClient
     */
    public static LocationClient newLocationClient(Context context, Type type, int scanSpan, BDLocationListener listener) {
        LocationClient client = new LocationClient(context, newOption(type, scanSpan));
        if (listener != null) client.registerLocationListener(listener);
        return client;
    }

    private static LocationClientOption newOption(Type type, int scanSpan) {
        if (scanSpan < 0) {
            switch (type) {
                case 网络定位:
                    scanSpan = 6000;
                    break;
                default:
                    scanSpan = 3000;
                    break;
            }
        }
        LocationClientOption locOption = new LocationClientOption();
        locOption.setOpenGps(type != Type.网络定位);
        locOption.setLocationMode(type == Type.网络定位 ? LocationMode.Battery_Saving : LocationMode.Hight_Accuracy);
        locOption.setCoorType("gcj02");
        locOption.setIsNeedAddress(type != Type.GPS定位);
        locOption.setScanSpan(scanSpan);
        return locOption;
    }

    /**
     * 生成在线预览的地图图片URL
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return 生成的URL
     */
    public static String mapPreviewURL(String latitude, String longitude) {
        return mapPreviewURL(Double.valueOf(latitude), Double.valueOf(longitude));
    }

    /**
     * 生成在线预览的地图图片URL
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return 生成的URL
     */
    public static String mapPreviewURL(double latitude, double longitude) {
        StringBuilder sb = new StringBuilder();
        double[] latlon = convertToBaiduCoordType(latitude, longitude);
        sb.append("http://api.map.baidu.com/staticimage/v2?")
          .append("ak=")
          .append("pt2W3vyG90CG0npRS2b02zpErAxnG7xI") //这个授权码专门用来在线预览...
          .append("&mcode=") //假的...
          .append("00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:19:87:01:28;me.skean.mappreview")
          .append("&center=")
          .append(latlon[1])//经度先传 ,下同
          .append(",")
          .append(latlon[0]) //纬度
          .append("&width=")
          .append(300)
          .append("&height=")
          .append(200)
          .append("&zoom=")
          .append(16)
          .append("&markers=")
          .append(latlon[1])//经度
          .append(",")
          .append(latlon[0]) //纬度
          .append("&copyright=")
          .append(1);
        return sb.toString();
    }

    /**
     * 生成在线预览的地图图片URL
     *
     * @param bdLocation 百度定位结果
     * @return 生成的URL
     */
    public static String mapPreviewURL(BDLocation bdLocation) {
        return mapPreviewURL(bdLocation.getLatitude(), bdLocation.getLongitude());
    }

    /**
     * 将国标系转换为百度坐标系
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return 转换后的坐标系 ,第一个是纬度 ,第二个是经度
     */
    public static double[] convertToBaiduCoordType(String latitude, String longitude) {
        return convertToBaiduCoordType(Double.valueOf(latitude), Double.valueOf(longitude));

    }

    /**
     * 将国标系转换为百度坐标系
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return 转换后的坐标系 ,第一个是纬度 ,第二个是经度
     */
    public static double[] convertToBaiduCoordType(double latitude, double longitude) {
        LatLng sourceLatLng = new LatLng(latitude, longitude);
        // 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return new double[]{desLatLng.latitude, desLatLng.longitude};
    }
}
