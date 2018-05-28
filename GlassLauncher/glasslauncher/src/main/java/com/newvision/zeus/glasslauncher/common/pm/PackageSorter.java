package com.newvision.zeus.glasslauncher.common.pm;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhangsong on 17-7-4.
 */

public class PackageSorter {
    private static final String TAG = "PackageWhiteList";

    /**
     * key: 包名或包名前缀
     * value: 权重，权重值越大，排序越靠前。
     */
    private Map<String, Integer> pkgMap = new HashMap<>();

    private Comparator<AppInfo> comparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo o1, AppInfo o2) {
            String[] supportPkgs = getSupportPkgs();

            int o1Level = 0;
            String o1PkgName = o1.getPkgName();
            if (pkgMap.containsKey(o1PkgName)) {
                o1Level = pkgMap.get(o1PkgName);
            } else {
                for (String suffix : supportPkgs) {
                    if (o1PkgName.contains(suffix)) {
                        o1Level = pkgMap.get(suffix);
                        break;
                    }
                }
            }

            int o2Level = 0;
            String o2PkgName = o2.getPkgName();
            if (pkgMap.containsKey(o2PkgName)) {
                o2Level = pkgMap.get(o2PkgName);
            } else {
                for (String suffix : supportPkgs) {
                    if (o2PkgName.contains(suffix)) {
                        o2Level = pkgMap.get(suffix);
                        break;
                    }
                }
            }

            Log.i(TAG, String.format("compare, %s: %d, %s: %d", o1.getPkgName(), o1Level, o2.getPkgName(), o2Level));

            return o2Level - o1Level;
        }
    };

    public PackageSorter() {
        //TODO: Only for test, change map contents when release.
        pkgMap.put("cn.ceyes.skyworth.videocall", 100);
        pkgMap.put("cn.ceyes.ceyes_glass_live_abb", 95);
        pkgMap.put("com.chaozh.iReaderFree", 90);
        pkgMap.put("cn.ceyes.ceyes_glass_live_electric", 85);
    }

    public String[] getSupportPkgs() {
        Set<String> set = pkgMap.keySet();
        return set.toArray(new String[0]);
    }

    public List<AppInfo> sort(List<AppInfo> list) {
        Collections.sort(list, comparator);
        return list;
    }
}
