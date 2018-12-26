package cn.eejing.colorflower.presenter.ble.notify;

import com.clj.fastble.exception.BleException;

public interface INotifyFailure {
    void onNotifyFailure(BleException exception);
}
