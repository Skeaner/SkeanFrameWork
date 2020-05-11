package base.net;

import android.text.TextUtils;

/**
 * 蒲公英VersionInfo
 */
public class PgyVersionInfo {
    /**
     * "appKey": "b04ef13e9876c756521070275801f12e",
     * "appType": "2",
     * "appIsLastest": "1",
     * "appFileName": "app-release.apk",
     * "appFileSize": "20320266",
     * "appName": "贺州云警务",
     * "appVersion": "1.0",
     * "appVersionNo": "1",
     * "appBuildVersion": "1",
     * "appIdentifier": "yzsm.com.hzcloud",
     * "appIcon": "5b80dc1519ed9df4ae87bd16d451cd2f",
     * "appDescription": "",
     * "appUpdateDescription": "内部测试",
     * "appScreenshots": "",
     * "appShortcutUrl": "hzcloud",
     * "appCreated": "2016-05-31 09:26:25",
     * "appUpdated": "2016-05-31 09:26:25",
     * "appQRCodeURL": "http://o1wjx1evz.qnssl.com/app/qrcode/hzcloud"
     */

    private String appKey;
    private String appType;
    private String appIsLastest;
    private String appFileName;
    private String appFileSize;
    private String appName;
    private String appVersion;
    private String appVersionNo;
    private String appBuildVersion;
    private String appIdentifier;
    private String appIcon;
    private String appDescription;
    private String appUpdateDescription;
    private String appScreenshots;
    private String appShortcutUrl;
    private String appCreated;
    private String appUpdated;
    private String appQRCodeURL;

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public void setAppIsLastest(String appIsLastest) {
        this.appIsLastest = appIsLastest;
    }

    public void setAppFileName(String appFileName) {
        this.appFileName = appFileName;
    }

    public void setAppFileSize(String appFileSize) {
        this.appFileSize = appFileSize;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setAppVersionNo(String appVersionNo) {
        this.appVersionNo = appVersionNo;
    }

    public void setAppBuildVersion(String appBuildVersion) {
        this.appBuildVersion = appBuildVersion;
    }

    public void setAppIdentifier(String appIdentifier) {
        this.appIdentifier = appIdentifier;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public void setAppUpdateDescription(String appUpdateDescription) {
        this.appUpdateDescription = appUpdateDescription;
    }

    public void setAppScreenshots(String appScreenshots) {
        this.appScreenshots = appScreenshots;
    }

    public void setAppShortcutUrl(String appShortcutUrl) {
        this.appShortcutUrl = appShortcutUrl;
    }

    public void setAppCreated(String appCreated) {
        this.appCreated = appCreated;
    }

    public void setAppUpdated(String appUpdated) {
        this.appUpdated = appUpdated;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppType() {
        return appType;
    }

    public String getAppIsLastest() {
        return appIsLastest;
    }

    public String getAppFileName() {
        return appFileName;
    }

    public String getAppFileSize() {
        return appFileSize;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppVersionNo() {
        return appVersionNo;
    }

    public String getAppBuildVersion() {
        return appBuildVersion;
    }

    public String getAppIdentifier() {
        return appIdentifier;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public String getAppUpdateDescription() {
        return appUpdateDescription;
    }

    public String getAppScreenshots() {
        return appScreenshots;
    }

    public String getAppShortcutUrl() {
        return appShortcutUrl;
    }

    public String getAppCreated() {
        return appCreated;
    }

    public String getAppUpdated() {
        return appUpdated;
    }

    public String getAppQRCodeURL() {
        return appQRCodeURL;
    }

    public void setAppQRCodeURL(String appQRCodeURL) {
        this.appQRCodeURL = appQRCodeURL;
    }

    public String getDownLoadURL(String apiKey, String appId) {
        return TextUtils.concat("http://www.pgyer.com/apiv1/app/install?_api_key=", apiKey, "&aId=", appId)
                        .toString();
    }
}
