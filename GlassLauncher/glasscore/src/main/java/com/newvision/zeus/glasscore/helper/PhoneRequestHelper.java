package com.newvision.zeus.glasscore.helper;

import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glasscore.protocol.helper.GlassPacketMessageHelper;
import com.newvision.zeus.glasscore.protocol.netty.client.GlassTcpClient;
import com.newvision.zeus.glasscore.protocol.netty.server.GlassTcpServer;
import com.newvision.zeus.glasscore.protocol.usb.accessory.GlassUsbAccessoryService;
import com.newvision.zeus.glasscore.protocol.usb.host.GlassUsbHostService;

/**
 * Created by yanjiatian on 2017/9/19.
 * 用于眼镜端向手机端请求的帮助类
 */

public class PhoneRequestHelper {
    private static PhoneRequestHelper mInstance;

    /**
     * 获取单例
     */
    public static PhoneRequestHelper getInstance() {
        if (mInstance == null) {
            mInstance = new PhoneRequestHelper();
        }
        return mInstance;
    }

    /**
     * 向Service里面注册callback监听
     *
     * @param binder
     * @param callback
     */
    public void registerListener(Object binder, IMessageCallback callback) {
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).registerListener(callback);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).registerListener(callback);
        }
    }


    /**
     * 取消Service里面注册callback监听
     *
     * @param binder
     * @param callback
     */
    public void unregisterListener(Object binder, IMessageCallback callback) {
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).unregisterListener(callback);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).unregisterListener(callback);
        }
    }

    /**
     * 打开手机端系统内置应用模块
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.PHONE_OPEN_APP_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void openInternalApp(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_OPEN_APP_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 打开眼镜端系统应用模块回应
     * @param binder
     * @param body
     * @param errorCode  状态码
     */
    public synchronized void openInternalAppAns(Object binder, String body, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_OPEN_APP_ANS, errorCode, body.getBytes());
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 退出手机端系统内置应用模块
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.PHONE_CLOSE_APP_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void closeInternalApp(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_CLOSE_APP_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 退出眼镜端内置应用模块回应
     *
     * @param binder
     * @param body
     * @param errorCode  状态码
     */
    public synchronized void closeInternalAppAns(Object binder, String body, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_CLOSE_APP_ANS, errorCode, body.getBytes());
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }


    /**
     * 请求开启手机端camera预览
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.PHONE_START_PREVIEW_ANS
     *
     * @param binder
     */
    public synchronized void startCameraPreview(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_START_PREVIEW_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 请求开启眼镜端camera预览回应
     *
     * @param binder
     * @param errorCode 状态码
     */
    public synchronized void startCameraPreviewAns(Object binder, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_START_PREVIEW_ANS, errorCode, null);
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 请求关闭手机端camera预览
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.PHONE_STOP_PREVIEW_ANS
     *
     * @param binder
     */
    public synchronized void stopCameraPreview(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_STOP_PREVIEW_ASK, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 请求关闭眼镜端camera预览回应
     *
     * @param binder
     * @param errorCode 状态码
     */
    public synchronized void stopCameraPreviewAns(Object binder, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_STOP_PREVIEW_ANS, errorCode, null);
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 眼镜端与手机端根据应用特定的需求，发送自定义的指令集，用户可以自定义指令的格式
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.PHONE_SEND_COMMAND_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void customCommand(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_SEND_COMMAND_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
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
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_SEND_COMMAND_ANS, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 眼镜端发起传输文件请求，分为上传和下载两种方式，如果是上传，需要携带文件名称，
     * 如果是下载需要携带眼镜端文件路径。得到应答响应之后，就开始传输文件
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.PHONE_START_LOAD_FILE_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void startLoadFile(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_START_LOAD_FILE_ASK, GlassErrorCode.OK, body.getBytes());
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 对眼镜端发起传输文件请求回应
     *
     * @param binder
     * @param errorCode
     */
    public synchronized void startLoadFileAns(Object binder, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_START_LOAD_FILE_ANS, errorCode, null);
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 请求开启眼镜端麦克风采集音频数据回应
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_START_AUDIO_ANS
     *
     * @param binder
     * @param body
     */
    public synchronized void startAudioAns(Object binder, String body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_START_AUDIO_ANS, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 请求关闭眼镜端麦克风回应
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.GLASS_STOP_AUDIO_ANS
     *
     * @param binder
     */
    public synchronized void stopAudioAns(Object binder) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_STOP_AUDIO_ANS, GlassErrorCode.OK, null);
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 当眼镜端发送文件完毕之后，需要调用此接口。手机端收到此命令ASK之后，说明当前文件传输完毕，
     * 可以根据之前获取的文件名存储该文件，并返回ANS命令。这样一个文件从眼镜端发送到手机端的过程就执行完了。
     * 需要得到返回结果，通过注册callback获取
     * tag==GlassMessageType.PHONE_STOP_LOAD_FILE_ANS
     *
     * @param binder
     */
    public synchronized void fileTransferFinish(Object binder, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.PHONE_STOP_LOAD_FILE_ASK, errorCode, null);
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    /**
     * 当眼镜端发送文件完毕之后，需要调用此接口,回应请求
     *
     * @param binder
     */
    public synchronized void fileTransferFinishAns(Object binder, byte errorCode) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_STOP_LOAD_FILE_ANS, errorCode, null);
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    public synchronized void sendVideoStream(Object binder, byte[] body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.LOAD_H264, GlassErrorCode.OK, body);
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    public synchronized void sendAudioFile(Object binder, byte[] body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.LOAD_AUDIO, GlassErrorCode.OK, body);
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

    public synchronized void sendFileData(Object binder, byte[] body) {
        GlassMessage message = GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.LOAD_FILE, GlassErrorCode.OK, body);
        if (binder instanceof GlassTcpServer.ServerServiceBinder) {
            ((GlassTcpServer.ServerServiceBinder) binder).sendMessage(message);
        } else {
            ((GlassUsbHostService.HostServiceBinder) binder).sendMessage(message);
        }
    }

}
