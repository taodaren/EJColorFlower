package cn.eejing.ejcolorflower.model.request;

public class MasterGroupListBean {
    private String groupName;
    private int devNum;
    private int startDmx;
    private String jetMode;

    public MasterGroupListBean(String groupName, int devNum, int startDmx, String jetMode) {
        this.groupName = groupName;
        this.devNum = devNum;
        this.startDmx = startDmx;
        this.jetMode = jetMode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getDevNum() {
        return devNum;
    }

    public void setDevNum(int devNum) {
        this.devNum = devNum;
    }

    public int getStartDmx() {
        return startDmx;
    }

    public void setStartDmx(int startDmx) {
        this.startDmx = startDmx;
    }

    public String getJetMode() {
        return jetMode;
    }

    public void setJetMode(String jetMode) {
        this.jetMode = jetMode;
    }
}
