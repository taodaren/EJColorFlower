package cn.eejing.ejcolorflower.device;

import android.support.annotation.NonNull;

@SuppressWarnings("unused")
public class Device {
    DeviceConfig config = null;
    DeviceState state = null;
    DeviceMaterialStatus materialStatus = null;

    private final String address;
    private long id = 0; // from server
    private boolean connected = false;

    public Device(@NonNull String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }


    public DeviceConfig getConfig() {
        return config;
    }

    /**
     * 清空设备配置
     */
    public void clearConfig() {
        this.config = null;
    }

    /**
     * 清空设备状态
     */
    public void clearState() {
        this.state = null;
    }

    public DeviceState getState() {
        return state;
    }

    public DeviceMaterialStatus getMaterialStatus() {
        return materialStatus;
    }


    public void setConfig(DeviceConfig config) {
        this.config = config;
    }

    public void setState(DeviceState mState) {
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
        return (config == null) ? id : config.mID;
    }

    public void setId(long id) {
        this.id = id;
    }

}
