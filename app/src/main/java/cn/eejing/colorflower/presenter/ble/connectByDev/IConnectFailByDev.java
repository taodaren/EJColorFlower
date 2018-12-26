package cn.eejing.colorflower.presenter.ble.connectByDev;

import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

public interface IConnectFailByDev {
    void onConnectFail(BleDevice bleDevice, BleException exception);
}
