package cn.eejing.ejcolorflower.model.event;

import cn.eejing.ejcolorflower.device.Device;

public class DeviceConnectEvent {
    private String id;
    private Device device;

    public DeviceConnectEvent(String id) {
        this.id = id;
    }

    public DeviceConnectEvent(String id, Device device) {
        this.id = id;
        this.device = device;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
