package com.newvision.zeus.glasslauncher.common.pm;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用显示黑名单，在该名单中的app package name将不会出现在app hub中。
 * <p>
 * Created by zhangsong on 17-7-6.
 */
public class PackageBlackList {
    private List<String> blackList = null;

    public PackageBlackList() {
        blackList = new ArrayList<>();

        blackList.add("android");
        blackList.add("com.android.backupconfirm");
        blackList.add("com.android.bluetooth");
        blackList.add("com.example.android.BluetoothChat");
        blackList.add("com.android.browser");
        blackList.add("com.android.calculator2");
        blackList.add("com.android.certinstaller");
        blackList.add("com.android.defcontainer");
        blackList.add("com.android.email");
        blackList.add("com.android.exchange");
        blackList.add("com.android.htmlviewer");
        blackList.add("com.android.inputmethod.latin");
        blackList.add("com.android.keychain");
        blackList.add("com.android.launcher");
        blackList.add("com.android.music");
        blackList.add("com.android.musicfx");
        blackList.add("com.android.packageinstaller");
        blackList.add("com.android.phone");
        blackList.add("com.android.providers.applications");
        blackList.add("com.android.providers.calendar");
        blackList.add("com.android.providers.contacts");
        blackList.add("com.android.providers.downloads");
        blackList.add("com.android.providers.downloads.ui");
        blackList.add("com.android.providers.drm");
        blackList.add("com.android.providers.media");
        blackList.add("com.android.providers.settings");
        blackList.add("com.android.providers.telephony");
        blackList.add("com.android.providers.userdictionary");
        blackList.add("com.android.provision");
        blackList.add("com.android.quicksearchbox");
        blackList.add("com.android.cellbroadcastreceiver");
        blackList.add("com.android.settings");
        blackList.add("com.android.mms");
        blackList.add("com.android.sharedstoragebackup");
        blackList.add("com.android.smspush");
        blackList.add("com.android.systemui");
        blackList.add("com.android.videoeditor");
        blackList.add("com.android.vpndialogs");
        blackList.add("com.android.gallery3d");
        blackList.add("com.android.apps.tag");
        blackList.add("com.android.calendar");
        blackList.add("com.android.contacts");
        blackList.add("com.android.deskclock");
        blackList.add("com.android.dreams.basic");
        blackList.add("com.android.dreams.phototable");
        blackList.add("com.android.galaxy4");
        blackList.add("com.android.inputdevices");
        blackList.add("com.android.inputmethod.pinyin");
        blackList.add("com.android.location.fused");
        blackList.add("com.android.magicsmoke");
        blackList.add("com.android.musicvis");
        blackList.add("com.android.nfc");
        blackList.add("com.android.noisefield");
        blackList.add("com.android.phasebeam");
        blackList.add("com.android.soundrecorder");
        blackList.add("com.android.wallpaper");
        blackList.add("com.android.wallpaper.holospiral");
        blackList.add("com.android.wallpaper.livepicker");

        blackList.add("board_id.com.ti");
        blackList.add("com.rxnetworks.rxnservicesxybrid");
        blackList.add("com.ti.bluetoothSCOapp");
        blackList.add("com.ti.fmrxapp");
        blackList.add("com.ti.fmtxapp");
        blackList.add("com.ti.gpsapp");
        blackList.add("com.ti.server");
        blackList.add("sensor.test");
        blackList.add("com.example.android.beam");

        blackList.add("com.newvision.zeus.glasslauncher");

        blackList.add("cn.ceyes.smartglasslauncher");
        blackList.add("cn.ceyes.inner.glass.videocall");
        blackList.add("cn.ceyes.inner.glass.voicerecognition");
        blackList.add("cn.ceyes.inner.glass.baidunavigation");
        blackList.add("cn.ceyes.voiceservice");
        blackList.add("cn.ceyes.oqc");

        // lg devices
        blackList.add("com.lge.camera");
        blackList.add("com.lge.music");
        blackList.add("com.lge.voicerecorder");

        blackList.add("com.sohu.inputmethod.sogou");
        blackList.add("com.google.zxing.client.android");
        blackList.add("com.iflytek.speechcloud");
        blackList.add("com.svox.pico");
        blackList.add("com.ti.omap4.android.camera");
        blackList.add("jp.co.omronsoft.openwnn");

        blackList.add("com.vuzix.carkitservices");

        blackList.add("com.example.qr_readerexample");
        blackList.add("com.example.videoview");
        blackList.add("com.google.speech.levelmeter");
        blackList.add("com.iesebre.DAM2.touch");
        blackList.add("com.example.lenovotestcamera_ver3");
        blackList.add("com.livecast.mobile");
    }

    public List<String> getBlackList() {
        return blackList;
    }
}
