package cn.eejing.ejcolorflower.device;

import android.support.annotation.NonNull;

/**
 * 发送命令接口
 */

public interface ISendCommand {
    void sendCommand(@NonNull Device device, @NonNull byte[] pkg);

    void sendCommand(@NonNull Device device, @NonNull byte[] pkg, OnReceivePackage callback);
}
