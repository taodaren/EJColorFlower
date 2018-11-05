package cn.eejing.colorflower.presenter;

import android.support.annotation.NonNull;

/**
 * 接收数据包回调
 */

public interface OnReceivePackage {
    void ack(@NonNull byte[] pkg);

    void timeout();
}
