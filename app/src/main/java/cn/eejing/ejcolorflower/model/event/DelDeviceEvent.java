package cn.eejing.ejcolorflower.model.event;

public class DelDeviceEvent {
    private String deviceId;

    public DelDeviceEvent(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
