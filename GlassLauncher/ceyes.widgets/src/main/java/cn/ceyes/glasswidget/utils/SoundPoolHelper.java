package cn.ceyes.glasswidget.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.util.SparseIntArray;

import cn.ceyes.widgets.R;

/**
 * Created by zhaoliang on 1/12/15.
 */
public class SoundPoolHelper {
    private static final String TAG = "SoundPoolHelper";
    public static final int SOUND_NOTIFICATION = 1001;
    public static final int SOUND_KEYPRESS = 1002;
    public static final int SOUND_TAKEPICTURE = 1003;

    private static boolean isHeadsetCon = false;
    private static Context mContext = null;
    private static SoundPool mSoundPool;// 声明一个SoundPool
    private static SparseIntArray music = new SparseIntArray();

    public static void playMusic(final int key) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSoundPool == null) {
                    if (!loadResourceByHeadsetStatus(isHeadsetCon)) {
                        return;
                    }
                }
                mSoundPool.play(music.get(key), 1, 1, 0, 0, 1);
            }
        }).start();
    }

    public static void initMusic(Context context) {
        mContext = context;
    }

    public static void uninit() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
    }

    public static boolean loadResourceByHeadsetStatus(boolean headSetCon) {
        if (mContext == null) {
            Log.e(TAG, "loadResource context is null ....");
            return false;
        }
        if (false) {
            // 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量STREAM_SYSTEM
//            mSoundPool = new SoundPool(10, AudioManager.STREAM_BLUETOOTH_SCO, 5);
        } else {
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        }

        AssetFileDescriptor file;

        file = mContext.getResources().openRawResourceFd(R.raw.notification);
        music.put(SOUND_NOTIFICATION, mSoundPool.load(file, 1));
        file = mContext.getResources().openRawResourceFd(R.raw.keypress);
        music.put(SOUND_KEYPRESS, mSoundPool.load(file, 1));
        file = mContext.getResources().openRawResourceFd(R.raw.sound_takepicture);
        music.put(SOUND_TAKEPICTURE, mSoundPool.load(file, 1));
        return true;
    }
}
