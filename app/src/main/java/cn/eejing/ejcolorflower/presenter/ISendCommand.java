package cn.eejing.ejcolorflower.presenter;

import android.support.annotation.NonNull;

import cn.eejing.ejcolorflower.device.Device;

/**
 * 发送命令接口
 */

public interface ISendCommand {
    void sendCommand(@NonNull Device device, @NonNull byte[] pkg);

    void sendCommand(@NonNull Device device, @NonNull byte[] pkg, OnReceivePackage callback);
}