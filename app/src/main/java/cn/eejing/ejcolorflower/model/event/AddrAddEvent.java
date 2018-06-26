package cn.eejing.ejcolorflower.model.event;

public class AddrAddEvent {
    private String addStatus;

    public AddrAddEvent(String addStatus) {
        this.addStatus = addStatus;
    }

    public String getAddStatus() {
        return addStatus;
    }

    public void setAddStatus(String addStatus) {
        this.addStatus = addStatus;
    }

}
