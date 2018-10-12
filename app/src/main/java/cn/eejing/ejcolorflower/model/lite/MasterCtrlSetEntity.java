package cn.eejing.ejcolorflower.model.lite;

import org.litepal.crud.LitePalSupport;

/**
 * 主控设置保存实体类
 */

public class MasterCtrlSetEntity extends LitePalSupport {
    private String devId;
    private int devNum;
    private int startDmx;
    private String jetMode;

    public MasterCtrlSetEntity() {
    }

    public MasterCtrlSetEntity(String devId, int devNum, int startDmx, String jetMode) {
        this.devId = devId;
        this.devNum = devNum;
        this.startDmx = startDmx;
        this.jetMode = jetMode;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
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
