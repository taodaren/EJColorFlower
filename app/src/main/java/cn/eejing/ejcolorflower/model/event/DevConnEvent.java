package cn.eejing.ejcolorflower.model.event;

public class DevConnEvent {
    private String mac;
    private String status;

    public DevConnEvent(String mac, String status) {
        this.mac = mac;
        this.status = status;
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
}
