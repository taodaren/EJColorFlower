package cn.eejing.ejcolorflower.model.event;

public class DmxZeroEvent {
    private int niDmx;

    public DmxZeroEvent(int niDmx) {
        this.niDmx = niDmx;
    }

    public int getNiDmx() {
        return niDmx;
    }

    public void setNiDmx(int niDmx) {
        this.niDmx = niDmx;
    }
}
