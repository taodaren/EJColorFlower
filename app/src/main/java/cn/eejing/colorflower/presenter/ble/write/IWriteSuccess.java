package cn.eejing.colorflower.presenter.ble.write;

public interface IWriteSuccess {
    void onWriteSuccess(int current, int total, byte[] justWrite);
}
