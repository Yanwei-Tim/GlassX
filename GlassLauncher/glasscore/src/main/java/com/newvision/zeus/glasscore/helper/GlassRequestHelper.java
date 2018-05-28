package com.newvision.zeus.glasscore.helper;

import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glasscore.protocol.helper.GlassPacketMessageHelper;
import com.newvision.zeus.glasscore.protocol.netty.client.GlassTcpClient;
import com.newvision.zeus.glasscore.protocol.usb.accessory.GlassUsbAccessoryService;

/**
 * Created by yanjiatian on 2017/9/15.
 * 用于手机端向眼镜端请求的帮助类
 */

public class GlassRequestHelper {
    private static GlassRequestHelper mInstance;

    /**
     * 获取单例
     */
    public static GlassRequestHelper getInstance() {
        if (mInstance == null) {
            mInstance = new GlassRequestHelper();
        }
        return mInstance;
    }

    /**
     * 登录 眼镜端
     *
     * @param binder
     */
    public void login(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_LOGIN_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取连接状态
     */
    public boolean getConnectStatus(Object binder) {
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            return ((GlassTcpClient.ClientServiceBinder) binder).getLoginGlassStatus();
        } else {
            return ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).getLoginGlassStatus();
        }
    }

    /**
     * 发送心跳命令
     * 获取心跳的监听条件
     * tag==GlassMessageType.GLASS_KEEP_ALIVE
     *
     * @param binder
     */
    public void keepAlive(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_KEEP_ALIVE, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 向Service里面注册callback监听
     *
     * @param binder
     * @param callback
     */
    public void registerListener(Object binder, IMessageCallback callback) {
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).registerListener(callback);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).registerListener(callback);
        }
    }


    /**
     * 取消Service里面注册callback监听
     *
     * @param binder
     * @param callback
     */
    public void unregisterListener(Object binder, IMessageCallback callback) {
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).unregisterListener(callback);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).unregisterListener(callback);
        }
    }

    /**
     * 获取眼镜端的系统时间
     * 如果需要得到返回结果，可以注册callback
     * tag==GlassMessageType.GLASS_GET_SYS_TIME_ANS
     *
     * @param binder
     */
    public synchronized void getSystemTime(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_SYS_TIME_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 设置眼镜端的系统时间
     * 如果需要得到返回结果，可以注册callback，
     * tag==GlassMessageType.GLASS_SET_SYS_TIME_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void setSystemTime(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_SET_SYS_TIME_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }


    /**
     * 获取眼镜端系统信息
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_GET_SYS_INFO_ANS
     *
     * @param binder
     */
    public synchronized void getSystemInfo(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_SYS_INFO_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端硬件信息，比如系统内存，剩余内存等
     * 如果需要得到返回结果，可以注册callback
     * tag==GlassMessageType.GLASS_GET_DEV_INFO_ANS
     *
     * @param binder
     */
    public synchronized void getDevInfo(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_DEV_INFO_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端音量信息
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_GET_VOLUME_ANS
     *
     * @param binder
     */
    public synchronized void getVolume(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_VOLUME_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端音量信息
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_SET_VOLUME_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void setVolume(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_SET_VOLUME_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端静音状态
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_GET_MUTE_ANS
     *
     * @param binder
     */
    public synchronized void getMute(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_MUTE_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 设置眼镜端静音状态
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_SET_MUTE_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void setMute(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_SET_MUTE_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端屏幕亮度
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_GET_BRIGHTNESS_ANS
     *
     * @param binder
     */
    public synchronized void getBrightness(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_BRIGHTNESS_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 设置眼镜端屏幕亮度
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_SET_BRIGHTNESS_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void setBrightness(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_SET_BRIGHTNESS_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端屏幕息屏时间
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_GET_LOCK_SCREEN_ANS
     *
     * @param binder
     */
    public synchronized void getLockScreenTime(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_LOCK_SCREEN_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 设置眼镜端屏幕息屏时间
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_SET_LOCK_SCREEN_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void setLockScreenTime(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_SET_LOCK_SCREEN_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端wifi连接信息
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_GET_WIFI_INFO_ANS
     *
     * @param binder
     */
    public synchronized void getWifiInfo(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_WIFI_INFO_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端bluetooth连接信息
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_GET_WIFI_INFO_ANS
     *
     * @param binder
     */
    public synchronized void getBluetoothInfo(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_BT_INFO_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端当前设置的自定义按键信息
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_GET_KEY_MSG_ANS
     *
     * @param binder
     */
    public synchronized void getKeyMsg(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_KEY_MSG_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 设置眼镜端自定义按键信息
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_SET_KEY_MSG_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void setKeyMsg(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_SET_KEY_MSG_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端安装应用的列表信息
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_GET_APP_LIST_ANS
     *
     * @param binder
     */
    public synchronized void getAppList(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_APP_LIST_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端安装应用的应用图标
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_GET_APP_ICON_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void getAppIcon(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_APP_ICON_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端当前语音命令词汇列表
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_GET_VOICE_LIST_ANS
     *
     * @param binder
     */
    public synchronized void getVoiceList(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_VOICE_LIST_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 设置眼镜端语音词汇列表
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_SET_VOICE_LIST_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void setVoiceList(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_SET_VOICE_LIST_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 获取眼镜端内置相机的配置参数
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_GET_CAMERA_PARA_ANS
     *
     * @param binder
     */
    public synchronized void getCameraPara(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_GET_CAMERA_PARA_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 设置眼镜端内置相机的配置参数
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_SET_CAMERA_PARA_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void setCameraPara(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_SET_CAMERA_PARA_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     *
     */

    /**
     * 打开眼镜端系统内置应用模块
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_OPEN_APP_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void openInternalApp(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_OPEN_APP_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 打开手机端系统内置应用模块回应
     *
     * @param binder
     * @param errorCode 状态码
     */
    public synchronized void openInternalAppAns(Object binder, String body, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_OPEN_APP_ANS, errorCode, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 退出眼镜端系统内置应用模块
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_CLOSE_APP_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void closeInternalApp(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_CLOSE_APP_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 退出手机端系统内置应用模块回应
     *
     * @param binder
     * @param errorCode 状态码
     */
    public synchronized void closeInternalAppAns(Object binder, String body, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_CLOSE_APP_ANS, errorCode, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }


    /**
     * 请求开启眼镜端camera预览
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_START_PREVIEW_ANS
     *
     * @param binder
     */
    public synchronized void startCameraPreview(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_START_PREVIEW_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 请求开启手机端camera预览回应
     *
     * @param binder
     * @param errorCode 状态码
     */
    public synchronized void startCameraPreviewAns(Object binder, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_START_PREVIEW_ANS, errorCode, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 请求关闭眼镜端camera预览
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_STOP_PREVIEW_ANS
     *
     * @param binder
     */
    public synchronized void stopCameraPreview(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_STOP_PREVIEW_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 请求关闭手机端camera预览回应
     *
     * @param binder
     * @param errorCode 状态码
     */
    public synchronized void stopCameraPreviewAns(Object binder, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_STOP_PREVIEW_ANS, errorCode, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 请求开启眼镜端麦克风采集音频数据
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_START_AUDIO_ASK
     *
     * @param binder
     * @param body
     */
    public synchronized void startAudio(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_START_AUDIO_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 请求关闭眼镜端麦克风，释放麦克风
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_STOP_AUDIO_ANS
     *
     * @param binder
     */
    public synchronized void stopAudio(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_STOP_AUDIO_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 眼镜端与手机端根据应用特定的需求，发送自定义的指令集，用户可以自定义指令的格式
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_SEND_COMMAND_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void customCommand(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_SEND_COMMAND_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 眼镜端与手机端根据应用特定的需求，发送自定义的指令集，用户可以自定义指令的格式回应
     * 对手机端发起的自定义命令回应
     *
     * @param binder
     * @param body
     */
    public synchronized void customCommandAns(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_SEND_COMMAND_ANS, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 眼镜端或者手机端发起传输文件请求，请求中携带请求传输文件的属性信息。暂定为文件的访问路径。
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_START_LOAD_FILE_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void startLoadFile(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_START_LOAD_FILE_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 对手机端发起传输文件请求回应
     *
     * @param binder
     * @param errorCode
     */
    public synchronized void startLoadFileAns(Object binder, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_START_LOAD_FILE_ANS, errorCode, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 当手机端发送文件完毕之后，需要调用此接口。眼镜端收到此命令ASK之后，说明当前文件传输完毕，
     * 可以根据之前获取的文件名存储该文件，并返回ANS命令。这样一个文件从手机端发送到眼镜端的过程就执行完了。
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_STOP_LOAD_FILE_ANS
     *
     * @param binder
     */
    public synchronized void fileTransferFinish(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_STOP_LOAD_FILE_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 当手机端发送文件完毕之后，需要调用此接口,回应请求
     *
     * @param binder
     */
    public synchronized void fileTransferFinishAns(Object binder, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_STOP_LOAD_FILE_ANS, errorCode, null);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    public synchronized void sendVideoStream(Object binder, byte[] body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.LOAD_H264, GlassErrorCode.OK, body);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    public synchronized void sendAudioFile(Object binder, byte[] body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.LOAD_AUDIO, GlassErrorCode.OK, body);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

    public synchronized void sendFileData(Object binder, byte[] body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.LOAD_FILE, GlassErrorCode.OK, body);
        if (binder instanceof GlassTcpClient.ClientServiceBinder) {
            ((GlassTcpClient.ClientServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbAccessoryService.AccessoryServiceBinder) binder).sendMessage(message);
        }
    }

}
