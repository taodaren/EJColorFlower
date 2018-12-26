package cn.eejing.colorflower.presenter.ble.startScan;

import com.clj.fastble.data.BleDevice;

import java.util.List;

public interface IScanFinished {
    void onScanFinished(List<BleDevice> scanResultList);
}
