package cn.eejing.colorflower.presenter.ble.connectByDev;

import android.bluetooth.BluetoothGatt;

import com.clj.fastble.data.BleDevice;

public interface IDisConnectedByDev {
    void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status);
}
