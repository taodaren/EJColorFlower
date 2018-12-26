package cn.eejing.colorflower.presenter.ble.startScan;

import com.clj.fastble.data.BleDevice;

public interface ILeScan {
    void onLeScan(BleDevice bleDevice);
}
