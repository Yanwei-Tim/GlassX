package com.newvision.zeus.glasslauncher.settings.deviceinfo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.newvision.zeus.glasslauncher.R;
import com.newvision.zeus.glasslauncher.common.helper.VersionProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.ceyes.glasswidget.singleview.GlassSingleView;

/**
 * Created by zhangsong on 4/10/15.
 */
public class AboutDeviceCardView extends GlassSingleView {

    private View contentView;

    private TextView mModelView;
    private TextView mLauncherVersionView;
    private TextView mKernalView;
    private TextView mRomVersionView;
    private TextView mXunFeiVersionView;

    public AboutDeviceCardView(Context context) {
        super(context);
        contentView = LayoutInflater.from(context).inflate(R.layout.activity_about_device, this);
        initViews();
        initValues();
    }

    @Override
    public void onViewFlingDown() {
        super.onViewFlingDown();

        ((Activity) context).finish();
    }

    private void initViews() {
        mModelView = (TextView) contentView.findViewById(R.id.txt_model);
        mLauncherVersionView = (TextView) contentView.findViewById(R.id.txt_version_android);
        mKernalView = (TextView) contentView.findViewById(R.id.txt_version_kernal);
        mRomVersionView = (TextView) contentView.findViewById(R.id.txt_version_rom);
        mXunFeiVersionView = (TextView) contentView.findViewById(R.id.txt_version_xunfei);
    }

    private void initValues() {
        mModelView.setText(R.string.txt_deviceinfo_model);
        mLauncherVersionView.setText(Build.VERSION.RELEASE);
        mKernalView.setText(getLinuxKernalVersion());
        mRomVersionView.setText(VersionProvider.getInstance(getContext()).getVersion());
        mXunFeiVersionView.setText(getXunFeiVersion());
    }

    private String getLinuxKernalVersion() {
        Process process = null;
        String mLinuxKernal = null;
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);

        String result = "";
        String line;
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result != "") {
            String Keyword = "version ";
            int index = result.indexOf(Keyword);
            line = result.substring(index + Keyword.length());
            index = line.indexOf(" ");
            mLinuxKernal = line.substring(0, index);
            index = line.indexOf("SMP");
            mLinuxKernal += line.substring(index - "SMP".length());
            return mLinuxKernal;
        }
        return "";
    }

    private String getXunFeiVersion() {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo("com.iflytek.speechcloud", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return info.versionName;
    }

}
