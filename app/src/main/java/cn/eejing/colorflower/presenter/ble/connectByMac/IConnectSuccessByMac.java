package cn.eejing.colorflower.presenter.ble.connectByMac;

import android.bluetooth.BluetoothGatt;

import com.clj.fastble.data.BleDevice;

public interface IConnectSuccessByMac {
    void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status);
}
