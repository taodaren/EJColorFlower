package cn.eejing.colorflower.presenter.ble.notify;

public interface ICharacteristicChanged {
    void onCharacteristicChanged(byte[] data);
}
