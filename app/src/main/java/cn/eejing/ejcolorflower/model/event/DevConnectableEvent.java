package cn.eejing.ejcolorflower.model.event;

public class DevConnectableEvent {
    private String mac;

    public DevConnectableEvent(String mac) {
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
