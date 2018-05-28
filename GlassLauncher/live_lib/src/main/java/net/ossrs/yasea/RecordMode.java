package net.ossrs.yasea;

/**
 * 用于指定编码类型
 * <p>
 * Created by zhangsong on 16-11-24.
 */

public class RecordMode {
    /**
     * 软编仅音频
     */
    public static final int SwOnlyAudio = 0x0101;
    /**
     * 软编仅视频
     */
    public static final int SwOnlyVideo = 0x0102;
    /**
     * 软编音频和视频
     */
    public static final int SwAudioAndVideo = 0x0103;
    /**
     * 硬编仅音频
     */
    public static final int HwOnlyAudio = 0x0201;
    /**
     * 硬编仅视频
     */
    public static final int HwOnlyVideo = 0x0202;
    /**
     * 硬编音频和视频
     */
    public static final int HwAudioAndVideo = 0x0203;
}