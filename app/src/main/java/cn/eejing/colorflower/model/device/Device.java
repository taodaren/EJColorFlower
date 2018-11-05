package cn.eejing.colorflower.model.device;

import android.support.annotation.NonNull;

@SuppressWarnings("unused")
public class Device {
    private DeviceConfig config;
    private DeviceStatus state;
    private DeviceMaterialStatus materialStatus;

    private final String address;
    private long id; // from server
    private boolean connected;

    public Device(@NonNull String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public DeviceConfig getConfig() {
        return config;
    }

    public DeviceStatus getState() {
        return state;
    }

    public DeviceMaterialStatus getMaterialStatus() {
        return materialStatus;
    }

    public void setConfig(DeviceConfig config) {
        this.config = config;
    }

    public void setState(DeviceStatus mState) {
        this.state = mState;
    }

    public void setMaterialStatus(DeviceMaterialStatus mMaterialStatus) {
        this.materialStatus = mMaterialStatus;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public long getId() {
        return (config == null) ? id : config.getID();
    }

    public void setId(long id) {
        this.id = id;
    }

    /** 清空设备配置 */
    public void clearConfig() {
        this.config = null;
    }

    /** 清空设备状态 */
    public void clearState() {
        this.state = null;
    }

}
