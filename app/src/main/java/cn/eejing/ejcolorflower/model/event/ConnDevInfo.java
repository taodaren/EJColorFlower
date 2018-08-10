package cn.eejing.ejcolorflower.model.event;

public class ConnDevInfo {
    private long devID;
    private int dmx;

    public ConnDevInfo(long devID, int dmx) {
        this.devID = devID;
        this.dmx = dmx;
    }

    public long getDevID() {
        return devID;
    }

    public void setDevID(long devID) {
        this.devID = devID;
    }

    public int getDmx() {
        return dmx;
    }

    public void setDmx(int dmx) {
        this.dmx = dmx;
    }
}
