package cn.eejing.colorflower.presenter.ble.connectByMac;

import android.bluetooth.BluetoothGatt;

import com.clj.fastble.data.BleDevice;

public interface IDisConnectedByMac {
    void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status);
}
