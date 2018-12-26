package cn.eejing.colorflower.presenter.ble.connectByMac;

import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

public interface IConnectFailByMac {
    void onConnectFail(BleDevice bleDevice, BleException exception);
}
