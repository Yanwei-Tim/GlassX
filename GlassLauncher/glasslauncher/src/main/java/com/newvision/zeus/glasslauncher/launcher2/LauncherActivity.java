package com.newvision.zeus.glasslauncher.launcher2;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.newvision.zeus.glasscore.base.GlassCoreServerActivity;
import com.newvision.zeus.glasslauncher.R;
import com.newvision.zeus.glasslauncher.common.helper.GlassTimeTickObserver;
import com.newvision.zeus.glasslauncher.common.helper.TimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.ceyes.glasswidget.gestures.GlassGestureDetector;
import cn.ceyes.glasswidget.gestures.GlassGestureListener;
import cn.ceyes.glasswidget.keyevents.GlassKeyEventDetector;
import cn.ceyes.glasswidget.keyevents.GlassKeyEventListener;

/**
 * Created by zhangsong on 17-7-6.
 */

public class LauncherActivity extends GlassCoreServerActivity {
    private static final String TAG = LauncherActivity.class.getSimpleName();

    private View parent;

    private TextView nowTimeText;

    private TimeFormatter timeFormatter;
    private AppHubLayout appHubLayout;
    private boolean isPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);

        this.parent = findViewById(R.id.parent);
        this.nowTimeText = (TextView) findViewById(R.id.nowtime);
        this.appHubLayout = (AppHubLayout) findViewById(R.id.app_hub);

        initKeyAndTouchListener();

        timeFormatter = TimeFormatter.getInstance("HH:mm");

        if (Build.VERSION.SDK_INT >= 23) {
            permission();
        } else {
            isPermission = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlassTimeTickObserver.getInstance().registerObserver(timeTickListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GlassTimeTickObserver.getInstance().unregisterObserver(timeTickListener);
    }

    private void setTime(String time) {
        this.nowTimeText.setText(time);
    }

    private GlassTimeTickObserver.ITimeTickListener timeTickListener = new GlassTimeTickObserver.ITimeTickListener() {
        @Override
        public void onTimeTick() {
            String time = timeFormatter.format(new Date());
            setTime(time);
        }
    };

    private void initKeyAndTouchListener() {
        final GlassGestureDetector gestureDetector = initGestureDetector();
        final GlassKeyEventDetector keyEventDetector = initKeyEventDetector();

        parent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(v, event);
            }
        });

        parent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return keyEventDetector.onKeyEvent(v, keyCode, event);
            }
        });
    }

    private GlassGestureDetector initGestureDetector() {
        return new GlassGestureDetector(this, new GlassGestureListener() {
            @Override
            public void onFlingLeft(View v) {
                super.onFlingLeft(v);
                appHubLayout.selectPrevious();
            }

            @Override
            public void onFlingLeftFast(View v) {
                super.onFlingLeftFast(v);
                appHubLayout.selectPrevious();
            }

            @Override
            public void onFlingRight(View v) {
                super.onFlingRight(v);
                appHubLayout.selectNext();
            }

            @Override
            public void onFlingRightFast(View v) {
                super.onFlingRightFast(v);
                appHubLayout.selectNext();
            }

            @Override
            public void onSingleTap(View v) {
                super.onSingleTap(v);
                Intent i = appHubLayout.getLaunchIntent();
                performLaunch(i);
            }
        });
    }

    private GlassKeyEventDetector initKeyEventDetector() {
        return new GlassKeyEventDetector(new GlassKeyEventListener() {
            @Override
            public void onPreviousKey() {
                super.onPreviousKey();
                appHubLayout.selectPrevious();
            }

            @Override
            public void onNextKey() {
                super.onNextKey();
                appHubLayout.selectNext();
            }

            @Override
            public void onEnterKey() {
                super.onEnterKey();
                Intent i = appHubLayout.getLaunchIntent();
                performLaunch(i);
            }
        });
    }

    private void performLaunch(Intent i) {
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void permission() {
        List<String> permissionLists = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionLists.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLists.add(Manifest.permission.CAMERA);
        }

        if (!permissionLists.isEmpty()) {//说明肯定有拒绝的权限
            ActivityCompat.requestPermissions(this, permissionLists.toArray(new String[permissionLists.size()]), 11);
        } else {
            Toast.makeText(this, "权限都授权了，可以搞事情了", Toast.LENGTH_SHORT).show();
            isPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 11:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意授权
                    isPermission = true;
                } else {
                    //用户拒绝授权
                }
                break;

        }
    }
}
