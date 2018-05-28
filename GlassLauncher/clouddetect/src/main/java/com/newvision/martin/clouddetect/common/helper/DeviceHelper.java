package com.newvision.martin.clouddetect.common.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.view.WindowManager;

import com.newvision.martin.clouddetect.common.util.LogUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.com.xpai.core.Manager;

import static net.ossrs.yasea.SrsEncoder.VCODEC;

/**
 * Created by Qing Jiwei on 2016/12/12.
 */

public class DeviceHelper {

    private static final String TAG = "DeviceInfor";
    private static boolean isMtk = false;

    // choose the video encoder by name.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static MediaCodecInfo chooseVideoEncoder(String name) {

        int nbCodecs = MediaCodecList.getCodecCount();

        for (int i = 0; i < nbCodecs; i++) {
            MediaCodecInfo mci = MediaCodecList.getCodecInfoAt(i);
            if (!mci.isEncoder()) {
                continue;
            }
            String[] types = mci.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(VCODEC)) {
                    LogUtil.i(TAG, String.format("vencoder %s types: %s", mci.getName(), types[j]));
                    if (name == null) {
                        return mci;
                    }
                    if (mci.getName().contains(name)) {
                        return mci;
                    }
                }
            }
        }
        return null;
    }

    public static List<Manager.Resolution> getMathResolution(Activity activity, int mix, int max) {

        List<Manager.Resolution> resolutionList = new ArrayList<>();

        Map<Double, Manager.Resolution> rateMap = new TreeMap<>(
                new Comparator<Double>() {
                    public int compare(Double obj1, Double obj2) {
                        // 降序排序
                        return obj1.compareTo(obj2);
                    }
                });
        MediaCodecInfo codeInfo = chooseVideoEncoder(null);
        if (codeInfo.getName().contains("MTK")) {
            isMtk = true;
        }
        WindowManager wm1 = activity.getWindowManager();
        double width = wm1.getDefaultDisplay().getWidth();
        double height = wm1.getDefaultDisplay().getHeight();
        double screenRate = width / height;
        LogUtil.i(TAG, "getMathResolution: screenRate" + screenRate);
        List<Manager.Resolution> resolutions = Manager.getSupportedVideoResolutions();
        Manager.deInit();
        if (resolutions != null) {
            LogUtil.i(TAG, "getMathResolution: isMtk " + isMtk);

            for (Manager.Resolution resoltion : resolutions) {
                if (resoltion.width < resoltion.height) {
                    continue;
                }
                if (resoltion.width < mix || resoltion.width > max) {
                    continue;
                }
                if (isMtk) {
                    if (resoltion.width % 32 != 0 || resoltion.height % 32 != 0) {
                        continue;
                    }
                }
                LogUtil.i(TAG, "getMathResolution: " + resoltion.width + "*" + resoltion.height);
                double currentMatch = (double) resoltion.width / (double) resoltion.height;
                LogUtil.i(TAG, "getMathResolution: currentmatch =" + currentMatch);
                double match = screenRate - currentMatch;
                LogUtil.i(TAG, "getMathResolution: match" + match);
                rateMap.put(Math.abs(match), resoltion);
            }
        }
        LogUtil.i(TAG, "getMathResolution: rateMap size" + rateMap.size());

        Set<Double> keySet = rateMap.keySet();
        for (Double key : keySet) {
            resolutionList.add(rateMap.get(key));
            LogUtil.i(TAG, "getMathResolution: width" + rateMap.get(key).width + "height" + rateMap.get(key).height);
        }

        return resolutionList;
    }
}
