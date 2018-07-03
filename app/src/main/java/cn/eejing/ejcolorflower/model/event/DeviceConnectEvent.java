package cn.eejing.ejcolorflower.model.event;

import cn.eejing.ejcolorflower.device.Device;

public class DeviceConnectEvent {
    private String info;
    private String mac;
    private Device device;

    public DeviceConnectEvent(String mac) {
        this.mac = mac;
    }

    public DeviceConnectEvent(String info, Device device) {
        this.info = info;
        this.device = device;
    }

    public DeviceConnectEvent(String info, String mac) {
        this.info = info;
        this.mac = mac;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
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
