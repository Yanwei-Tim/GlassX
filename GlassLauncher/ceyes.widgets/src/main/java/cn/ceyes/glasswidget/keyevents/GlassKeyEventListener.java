package cn.ceyes.glasswidget.keyevents;

import cn.ceyes.glasswidget.utils.SoundPoolHelper;

/**
 * Created by liusong on 2/2/15.
 */
public class GlassKeyEventListener {
    public void onPreviousKey() {
        SoundPoolHelper.playMusic(SoundPoolHelper.SOUND_KEYPRESS);
    }

    public void onNextKey() {
        SoundPoolHelper.playMusic(SoundPoolHelper.SOUND_KEYPRESS);
    }

    public void onBackKey() {
        SoundPoolHelper.playMusic(SoundPoolHelper.SOUND_KEYPRESS);
    }

    public void onEnterKey() {
        SoundPoolHelper.playMusic(SoundPoolHelper.SOUND_KEYPRESS);
    }
}
