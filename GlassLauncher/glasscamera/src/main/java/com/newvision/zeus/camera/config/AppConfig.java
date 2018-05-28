package com.newvision.zeus.camera.config;


/**
 * Glass setting
 * Created by Qing Jiwei on 5/21/17.
 */
public class AppConfig {

    private static final String TAG = "AppConfig";

    private ConfigService mConfigService;

    private String deviceSn;  //device sn

    //video setting
    private String videoHeight;  //resolution of the high
    private String videoWidth;   //resolution of the wide
    private String resolution;   //resolution eg:720P
    private String bps;       //Bit rate
    private String bidf;       //Bit rate

    //photo setting
    private String photoHeight;  //take picture resolution of the high
    private String photoWidth;   //take picture resolution  of the wide


    private static AppConfig instance = new AppConfig();

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public AppConfig() {
        mConfigService = new ConfigService(GlassConstants.SP_Address);
        load();
    }

    public void save() {

        mConfigService.putString("deviceSn", deviceSn);
        mConfigService.putString("videoWidth", videoWidth);
        mConfigService.putString("videoHeight", videoHeight);
        mConfigService.putString("resolution", resolution);
        mConfigService.putString("bps", bps);

        mConfigService.putString("photoWidth", photoWidth);
        mConfigService.putString("photoHeight", photoHeight);
        mConfigService.commit();
    }

    private void load() {

        deviceSn = mConfigService.getString("deviceSn", "");

        videoWidth = mConfigService.getString("videoWidth", "1080");
        videoHeight = mConfigService.getString("videoHeight", "720");
        resolution = mConfigService.getString("resolution", "720P");
        bps = mConfigService.getString("bps", "3000");

        photoWidth = mConfigService.getString("photoWidth", "720");
        photoHeight = mConfigService.getString("photoHeight", "480");

    }

    public void clearData() {

        mConfigService.putString("deviceSn", "");

        mConfigService.putString("videoWidth", "");
        mConfigService.putString("videoHeight", "");
        mConfigService.putString("resolution", "");
        mConfigService.putString("bps", "");

        mConfigService.putString("photoWidth", "");
        mConfigService.putString("photoHeight", "");
        mConfigService.commit();
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public AppConfig setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
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

    public String getPhotoHeight() {
        return photoHeight;
    }

    public AppConfig setPhotoHeight(String photoHeight) {
        this.photoHeight = photoHeight;
        return this;
    }

    public String getPhotoWidth() {
        return photoWidth;
    }

    public AppConfig setPhotoWidth(String photoWidth) {
        this.photoWidth = photoWidth;
        return this;
    }

}
