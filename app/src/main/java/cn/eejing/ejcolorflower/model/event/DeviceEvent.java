package cn.eejing.ejcolorflower.model.event;

public class DeviceEvent {
    private String id;

    public DeviceEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
