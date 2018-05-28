package com.newvision.zeus.glassmanager.common.decoder;


import java.util.List;
import java.util.Vector;

/**
 * Created by Qing Jiwei on 2016/12/14.
 */

public class StreamManager implements ReleaseAble {

    private static StreamManager instance;

    //whether to start
    private static boolean isRun = false;
    //whether to push frame
    private boolean upload = false;

    private Vector<VideoFrame> showPool = new Vector<>();
    private Vector<VideoFrame> pushPool = new Vector<>();

    public static StreamManager getInstance() {
        if (instance == null) {
            instance = new StreamManager();
        }
        return instance;
    }


    public void addStream(VideoFrame stream) {
        if (!isRun) {
            return;
        }
        if (upload) {
            showPool.add(stream);
            pushPool.add(stream);
        } else {
            showPool.add(stream);
        }
    }


    /**
     * to remove a frame from show vector
     *
     * @param frame a frame of stream
     */
    public void removeShow(VideoFrame frame) {
        if (showPool.contains(frame)) {
            showPool.remove(frame);
        }
    }

    /**
     * to remove a frame from push vector
     *
     * @param frame a frame of stream
     */
    public void removePush(VideoFrame frame) {
        if (pushPool.contains(frame)) {
            pushPool.remove(frame);
        }
    }

    public List<VideoFrame> getshowPool() {
        return showPool;
    }

    public List<VideoFrame> getpushPool() {
        return pushPool;
    }

    public void onStart(boolean isUpload) {
        upload = isUpload;
        if (upload) {
            showPool.clear();
            pushPool.clear();
            isRun = true;
        } else {
            showPool.clear();
            isRun = true;
        }
    }

    @Override
    public void startRelease() throws Exception {
        if (upload) {
            isRun = false;
            showPool.clear();
            pushPool.clear();
        } else {
            isRun = false;
            showPool.clear();
        }
        upload = false;
    }
}
