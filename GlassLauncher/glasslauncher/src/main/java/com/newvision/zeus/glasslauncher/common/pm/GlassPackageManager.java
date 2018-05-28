package com.newvision.zeus.glasslauncher.common.pm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhangsong on 17-7-4.
 */

public class GlassPackageManager {
    private static final String TAG = "GlassPackageManager";

    private static GlassPackageManager instance = null;

    private PackageManager pm;
    private PackageBlackList blackList;
    private PackageSorter packageSorter;

    public static GlassPackageManager getInstance(Context context) {
        if (instance == null) {
            synchronized (GlassPackageManager.class) {
                if (instance == null) {
                    instance = new GlassPackageManager(context);
                }
            }
        }
        return instance;
    }

    public List<AppInfo> getSortedAppInfos() {
        List<AppInfo> appInfos = new ArrayList<>();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
        // 调用系统排序 ， 根据name排序
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));

        List<String> blackListGet = blackList.getBlackList();

        for (ResolveInfo reInfo : resolveInfos) {
            String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
            Log.i(TAG, "getSortedAppInfos: " + pkgName);

            boolean match = false;
            for (String black : blackListGet) {
                if (black.equals(pkgName)) {
                    match = true;
                    break;
                }
            }
            if (match) continue;

            String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
            String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
            Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
            // 为应用程序的启动Activity 准备Intent
            Intent launchIntent = new Intent();
            launchIntent.setComponent(new ComponentName(pkgName,
                    activityName));
            // 创建一个AppInfo对象，并赋值
            AppInfo appInfo = new AppInfo();
            appInfo.setAppLabel(appLabel);
            appInfo.setPkgName(pkgName);
            appInfo.setAppIcon(icon);
            appInfo.setIntent(launchIntent);
            appInfos.add(appInfo); // 添加至列表中
            System.out.println(appLabel + " activityName---" + activityName
                    + " pkgName---" + pkgName);
        }

        packageSorter.sort(appInfos);

        return appInfos;
    }

    private GlassPackageManager(Context context) {
        pm = context.getPackageManager();
        blackList = new PackageBlackList();
        packageSorter = new PackageSorter();
    }
}
