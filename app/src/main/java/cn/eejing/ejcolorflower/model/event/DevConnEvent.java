package cn.eejing.ejcolorflower.model.event;

import cn.eejing.ejcolorflower.model.device.DeviceConfig;
import cn.eejing.ejcolorflower.model.device.DeviceStatus;

public class DevConnEvent {
    private long id;
    private String mac;
    private String status;
    private DeviceStatus deviceStatus;
    private DeviceConfig deviceConfig;

    public DevConnEvent(String mac, String status) {
        this.mac = mac;
        this.status = status;
    }

    public DevConnEvent(long id, String mac, String status, DeviceStatus deviceStatus, DeviceConfig deviceConfig) {
        this.id = id;
        this.mac = mac;
        this.status = status;
        this.deviceStatus = deviceStatus;
        this.deviceConfig = deviceConfig;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public DeviceConfig getDeviceConfig() {
        return deviceConfig;
    }

    public void setDeviceConfig(DeviceConfig deviceConfig) {
        this.deviceConfig = deviceConfig;
    }
}
