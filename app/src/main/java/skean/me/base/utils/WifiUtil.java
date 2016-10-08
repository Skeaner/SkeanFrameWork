package skean.me.base.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import java.util.List;

@SuppressWarnings("unused")
public class WifiUtil {
    private WifiManager wifiManager;
    private WifiLock wifiLock;

    public WifiUtil(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 打开WIFI
     */
    public void enableWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭WIFI
     */
    public void disableWifi() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 检查当前WIFI状态
     *
     * @return wifi的状态 ,有以下几种
     * @see WifiManager#WIFI_STATE_DISABLED
     * @see WifiManager#WIFI_STATE_DISABLING
     * @see WifiManager#WIFI_STATE_ENABLED
     * @see WifiManager#WIFI_STATE_ENABLING
     * @see WifiManager#WIFI_STATE_UNKNOWN
     */
    public int getWifiState() {
        return wifiManager.getWifiState();
    }

    /**
     * 锁定WifiLock
     */
    public void acquireWifiLock() {
        if (wifiLock == null) wifiLock = wifiManager.createWifiLock("WifiLock");
        wifiLock.acquire();
    }

    /**
     * 解锁WifiLock
     */
    public void releaseWifiLock() {
        if (wifiLock.isHeld()) {
            wifiLock.release();
        }
    }

    /**
     * 获取所有记录的网络配置
     *
     * @return WifiConfiguration列表
     */
    public List<WifiConfiguration> getSavedWifiConfigurations() {
        return wifiManager.getConfiguredNetworks();
    }

    /**
     * 保存一个网络设置, 返回其NetId
     *
     * @param wifiConf 网络设置
     * @return netId, 连接用
     */
    public int addWifiConfiguration(WifiConfiguration wifiConf) {
        return wifiManager.addNetwork(wifiConf);
    }

    /**
     * 连接到指定netId的网络
     *
     * @param netId 对应WifiConfiguration的netId
     */
    public void connect(int netId) {
        // 连接配置好的指定ID的网络
        wifiManager.enableNetwork(netId, true);
    }

    /**
     * 断开指定ID的网络
     *
     * @param netId id
     */
    public void disconnect(int netId) {
        wifiManager.disableNetwork(netId);
        wifiManager.disconnect();
    }

    /**
     * 开始扫描wifi
     */
    public void startScan() {
        wifiManager.startScan();
    }

    /**
     * 得到网络列表
     *
     * @return wifi的扫描结果的队列
     */
    public List<ScanResult> getWifiScanResults() {
        return wifiManager.getScanResults();
    }

    /**
     * 得等到连接wifi热点信息
     *
     * @return 信息包字符串
     */
    public WifiInfo getWifiInfo() {
        return wifiManager.getConnectionInfo();
    }

    /**
     * 获得当前网卡MAC地址
     *
     * @return mac地址字符串
     */
    public String getMacAddress() {
        WifiInfo wifiInfo = getWifiInfo();
        return (wifiInfo == null) ? null : wifiInfo.getMacAddress();
    }

    /**
     * 得到当前接入点的BSSID
     *
     * @return ssid字符串
     */
    public String getBSSID() {
        WifiInfo wifiInfo = getWifiInfo();
        return (wifiInfo == null) ? null : wifiInfo.getBSSID();
    }

    /**
     * 得到当前接入点的SSID
     *
     * @return 当前接入点的ssid
     */
    public String getSSID() {
        WifiInfo wifiInfo = getWifiInfo();
        return (wifiInfo == null) ? null : wifiInfo.getSSID();
    }

    /**
     * 得到当前IP地址
     *
     * @return ip地址
     */
    public int getIPAddress() {
        WifiInfo wifiInfo = getWifiInfo();
        return (wifiInfo == null) ? -1 : wifiInfo.getIpAddress();
    }

    /**
     * 得到连接的netWorkId
     *
     * @return netWorkId
     */
    public int getNetworkId() {
        WifiInfo wifiInfo = getWifiInfo();
        return (wifiInfo == null) ? -1 : wifiInfo.getNetworkId();
    }

    /**
     * 获取指定ssid的ap设置信息
     *
     * @return ap热点配置, 不存在则返回null
     */
    public WifiConfiguration getApConfiguration(String ssid) {
        WifiConfiguration apConf = null;
        for (WifiConfiguration conf : getSavedWifiConfigurations()) {
            if (conf != null && conf.SSID != null && conf.SSID.equals(quoteText(ssid))) {
                apConf = conf;
                break;
            }
        }
        return apConf;
    }

    /**
     * 根据指定ssid , 密码创建ap热点
     *
     * @param ssid 热点ssid
     * @param pwd  热点密码
     * @return 热点的设置信息
     */
    public WifiConfiguration createApConfig(String ssid, String pwd) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = quoteText(ssid);
        config.preSharedKey = quoteText(pwd);
        // 配置热点加密方式wpa
        config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
        config.status = WifiConfiguration.Status.ENABLED;
        return config;
    }

    /**
     * 给WifiConfiguration的ssid和pwd加上"\\"
     *
     * @param text 原始字符串
     * @return 添加了"\\"的字符串
     */
    private String quoteText(String text) {
        return "\"" + text + "\"";
    }
}
