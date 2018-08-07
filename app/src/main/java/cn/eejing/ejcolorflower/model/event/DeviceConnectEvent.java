package cn.eejing.ejcolorflower.model.event;

import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.DeviceStatus;

public class DeviceConnectEvent {
    private String info;
    private Device device;
    private String mac;
    private DeviceStatus state;
    private DeviceConfig config;

    public DeviceConnectEvent(String info, String mac) {
        this.info = info;
        this.mac = mac;
    }

    public DeviceConnectEvent(String info, String mac, DeviceStatus state, DeviceConfig config) {
        this.info = info;
        this.mac = mac;
        this.state = state;
        this.config = config;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public DeviceStatus getState() {
        return state;
    }

    public void setState(DeviceStatus state) {
        this.state = state;
    }

    public DeviceConfig getConfig() {
        return config;
    }

    public void setConfig(DeviceConfig config) {
        this.config = config;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
