package com.newvision.zeus.glasslauncher.settings.volume;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by zhangsong on 1/5/15.
 */
public class  GlassVolumeHelper {

    private static final String TAG = "GlassVolumeHelper";

    private AudioManager mAudioManager;

    private int dtmfMax = 0;
    private int notificationMax = 0;
    private int systemMax = 0;
    private int alarmMax = 0;
    private int ringMax = 0;
    private int callMax = 0;
    private int btMax = 0;
    private int musicMax = 0;
    private int STREAM_TYPE;
    public static int STREAM_ALL = -1;

    public GlassVolumeHelper(Context context) {
        this(context, STREAM_ALL);
    }

    public GlassVolumeHelper(Context context, int streamType) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        musicMax = getMaxVolume(AudioManager.STREAM_MUSIC);
        dtmfMax = getMaxVolume(AudioManager.STREAM_DTMF);
        notificationMax = getMaxVolume(AudioManager.STREAM_NOTIFICATION);
        systemMax = getMaxVolume(AudioManager.STREAM_SYSTEM);
        alarmMax = getMaxVolume(AudioManager.STREAM_ALARM);
        ringMax = getMaxVolume(AudioManager.STREAM_RING);
        callMax = getMaxVolume(AudioManager.STREAM_VOICE_CALL);
//        btMax = getMaxVolume(AudioManager.STREAM_BLUETOOTH_SCO);
        STREAM_TYPE = streamType;
    }

    public void setStreamType(int streamType) {
        STREAM_TYPE = streamType;
    }

    public int getMaxVolume() {
        return STREAM_TYPE == STREAM_ALL ? getMaxVolume(AudioManager.STREAM_MUSIC) : getMaxVolume(STREAM_TYPE);
    }

    public int getCurrentVolume() {
        return STREAM_TYPE == STREAM_ALL ? getCurrentVolume(AudioManager.STREAM_MUSIC) : getCurrentVolume(STREAM_TYPE);
    }

    private int getCurrentVolume(int type) {
        return mAudioManager.getStreamVolume(type);
    }

    private int getMaxVolume(int type) {
        return mAudioManager.getStreamMaxVolume(type);
    }

    public String getPercentVolume() {
        int current = getCurrentVolume();
        int max = getMaxVolume();
        return String.format("%.0f", Float.parseFloat(current + "") / max * 100) + "%";
    }

    private boolean setVolume(int current, int type) {
        int before = getCurrentVolume();
        mAudioManager.setStreamVolume(type, current, AudioManager.FLAG_PLAY_SOUND);
        int after = getCurrentVolume();
        if (before == after)
            return false;
        return true;
    }

    public boolean setVolume(int current) {

        if (STREAM_TYPE != STREAM_ALL) {
            return setVolume(current, STREAM_TYPE);
        }

        Log.i(TAG, "want to : " + current);

        int before = getCurrentVolume();
        Log.i(TAG, "before : " + before);

        float percent = Float.parseFloat(current + "") / musicMax;

        // 设定系统声音大小
        mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF, (int) (percent * dtmfMax),
                AudioManager.FLAG_PLAY_SOUND);
        Log.i(TAG, "STREAM_DTMF : " + getCurrentVolume(AudioManager.STREAM_DTMF));

        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
                (int) (percent * notificationMax), AudioManager.FLAG_PLAY_SOUND);
        Log.i(TAG, "STREAM_NOTIFICATION : " + getCurrentVolume(AudioManager.STREAM_NOTIFICATION));

        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                (int) (percent * systemMax), AudioManager.FLAG_PLAY_SOUND);
        Log.i(TAG, "STREAM_SYSTEM : " + getCurrentVolume(AudioManager.STREAM_SYSTEM));

        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, (int) (percent * alarmMax),
                AudioManager.FLAG_PLAY_SOUND);
        Log.i(TAG, "STREAM_ALARM : " + getCurrentVolume(AudioManager.STREAM_ALARM));

        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, (int) (percent * ringMax),
                AudioManager.FLAG_PLAY_SOUND);
        Log.i(TAG, "STREAM_RING : " + getCurrentVolume(AudioManager.STREAM_RING));

        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                (int) (percent * callMax), AudioManager.FLAG_PLAY_SOUND);
        Log.i(TAG, "STREAM_VOICE_CALL : " + getCurrentVolume(AudioManager.STREAM_VOICE_CALL));

//        mAudioManager.setStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO,
//                (int) (percent * btMax), AudioManager.FLAG_PLAY_SOUND);
//        Log.i(TAG, "STREAM_BLUETOOTH_SCO : " + getCurrentVolume(AudioManager.STREAM_BLUETOOTH_SCO));

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current,
                AudioManager.FLAG_PLAY_SOUND);

        int after = getCurrentVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "after : " + after);

        if (before == after)
            return false;

        return true;
    }

}
