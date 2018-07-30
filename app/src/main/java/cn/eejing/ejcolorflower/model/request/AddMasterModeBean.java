package cn.eejing.ejcolorflower.model.request;

public class AddMasterModeBean {
    private String mode;
    private long millis;// 时间戳

    public AddMasterModeBean() {
    }

    public AddMasterModeBean(String mode, long millis) {
        this.mode = mode;
        this.millis = millis;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
}
