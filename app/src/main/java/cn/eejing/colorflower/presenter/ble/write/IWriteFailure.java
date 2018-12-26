package cn.eejing.colorflower.presenter.ble.write;

import com.clj.fastble.exception.BleException;

public interface IWriteFailure {
    void onWriteFailure(BleException exception);
}
