package cn.eejing.ejcolorflower.model.lite;

import org.litepal.crud.LitePalSupport;

public class MasterCtrlNumEntity extends LitePalSupport {
    private String devId;          // 设备 ID
    private String devNum;         // 设备数量
    private String starDmx;        // 起始 DMX

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getDevNum() {
        return devNum;
    }

    public void setDevNum(String devNum) {
        this.devNum = devNum;
    }

    public String getStarDmx() {
        return starDmx;
    }

    public void setStarDmx(String starDmx) {
        this.starDmx = starDmx;
    }
}
