package cn.ceyes.glasswidget.gestures;

import android.view.View;

import cn.ceyes.glasswidget.utils.SoundPoolHelper;

/**
 * Created by zhangsong on 1/26/15.
 */
public class GlassGestureListener {

    public void onSingleTap(View v) {
        SoundPoolHelper.playMusic(SoundPoolHelper.SOUND_KEYPRESS);
    }

    public void onFlingLeft(View v) {
        SoundPoolHelper.playMusic(SoundPoolHelper.SOUND_KEYPRESS);
    }

    public void onFlingLeftFast(View v) {
        SoundPoolHelper.playMusic(SoundPoolHelper.SOUND_KEYPRESS);
    }

    public void onFlingRight(View v) {
        SoundPoolHelper.playMusic(SoundPoolHelper.SOUND_KEYPRESS);
    }

    public void onFlingRightFast(View v) {
        SoundPoolHelper.playMusic(SoundPoolHelper.SOUND_KEYPRESS);
    }
}
