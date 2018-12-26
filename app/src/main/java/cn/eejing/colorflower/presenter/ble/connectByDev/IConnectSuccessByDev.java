package cn.eejing.colorflower.presenter.ble.connectByDev;

import android.bluetooth.BluetoothGatt;

import com.clj.fastble.data.BleDevice;

public interface IConnectSuccessByDev {
    void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status);
}
