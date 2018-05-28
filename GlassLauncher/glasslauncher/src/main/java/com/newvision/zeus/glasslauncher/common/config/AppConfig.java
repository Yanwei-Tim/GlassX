package com.newvision.zeus.glasslauncher.common.config;


/**
 * Glass setting
 * Created by Qing Jiwei on 5/21/17.
 */
public class AppConfig {

    private static final String TAG = "AppConfig";

    private ConfigHelper mConfigHelper;
    private String ipHost;  //host id

    private String deviceSn;  //device sn
    //video setting
    private String videoHeight;  //resolution of the high
    private String videoWidth;   //resolution of the wide
    private String resolution;   //resolution eg:720P
    private String bps;       //Bit rate

    private String bidf;       //Bit


    private static AppConfig instance = new AppConfig();

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public AppConfig() {
        mConfigHelper = new ConfigHelper("glass_launcher");
        load();
    }

    public void save() {

        mConfigHelper.putString("deviceSn", deviceSn);
        mConfigHelper.putString("ipHost", ipHost);
        mConfigHelper.putString("videoWidth", videoWidth);
        mConfigHelper.putString("videoHeight", videoHeight);
        mConfigHelper.putString("resolution", resolution);
        mConfigHelper.putString("bps", bps);

        mConfigHelper.commit();
    }

    private void load() {

        deviceSn = mConfigHelper.getString("deviceSn", "");
        ipHost = mConfigHelper.getString("ipHost", "");

        videoWidth = mConfigHelper.getString("videoWidth", "1280");
        videoHeight = mConfigHelper.getString("videoHeight", "720");
        resolution = mConfigHelper.getString("resolution", "720P");
        bps = mConfigHelper.getString("bps", "5000");


    }

    public void clearData() {

        mConfigHelper.putString("deviceSn", "");
        mConfigHelper.putString("ipHost", "");

        mConfigHelper.putString("videoWidth", "");
        mConfigHelper.putString("videoHeight", "");
        mConfigHelper.putString("resolution", "");
        mConfigHelper.putString("bps", "");

        mConfigHelper.commit();
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public AppConfig setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
        return this;
    }

    public String getIpHost() {
        return ipHost;
    }

    public AppConfig setIpHost(String ipHost) {
        this.ipHost = ipHost;
        return this;
    }

    public String getVideoHeight() {
        return videoHeight;
    }

    public AppConfig setVideoHeight(String videoHeight) {
        this.videoHeight = videoHeight;
        return this;
    }

    public String getVideoWidth() {
        return videoWidth;
    }

    public AppConfig setVideoWidth(String videoWidth) {
        this.videoWidth = videoWidth;
        return this;
    }

    public String getResolution() {
        return resolution;
    }

    public AppConfig setResolution(String resolution) {
        this.resolution = resolution;
        return this;
    }

    public String getBps() {
        return bps;
    }

    public AppConfig setBps(String bps) {
        this.bps = bps;
        return this;
    }

}
