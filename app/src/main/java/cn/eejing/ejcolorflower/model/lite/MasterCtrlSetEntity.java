package cn.eejing.ejcolorflower.model.lite;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * 主控设置保存实体类
 */

public class MasterCtrlSetEntity extends LitePalSupport {
    private String devId;
    private int devNum;
    private int startDmx;
    private List<MasterCtrlModeEntity> mList;

    public MasterCtrlSetEntity(String devId, int devNum, int startDmx, List<MasterCtrlModeEntity> list) {
        this.devId = devId;
        this.devNum = devNum;
        this.startDmx = startDmx;
        this.mList = list;
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

    public List<MasterCtrlModeEntity> getList() {
        return mList;
    }

    public void setList(List<MasterCtrlModeEntity> list) {
        this.mList = list;
    }
}
