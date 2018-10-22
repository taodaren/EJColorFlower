package cn.eejing.ejcolorflower.model.request;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.List;

import cn.eejing.ejcolorflower.model.lite.MasterCtrlModeEntity;

/**
 * 主控分组信息实体类
 */

public class MasterGroupListBean extends LitePalSupport implements Serializable {
    private String devId;                           // 设备ID
    private String groupName;                       // 分组名称
    private int isSelectedGroup;                    // 是否选中分组 1-选中 2-未选中
    private int isSelectedMaster;                   // 是否选中包含主控 1-选中 2-未选中
    private int jetTime;                            // 喷射时间
    private int devNum;                             // 设备数量
    private int startDmx;                           // 起始DMX
    private List<MasterCtrlModeEntity> jetModes;    // 喷射效果列表

    public MasterGroupListBean() {
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getIsSelectedGroup() {
        return isSelectedGroup;
    }

    public void setIsSelectedGroup(int isSelectedGroup) {
        this.isSelectedGroup = isSelectedGroup;
    }

    public int getIsSelectedMaster() {
        return isSelectedMaster;
    }

    public void setIsSelectedMaster(int isSelectedMaster) {
        this.isSelectedMaster = isSelectedMaster;
    }

    public int getJetTime() {
        return jetTime;
    }

    public void setJetTime(int jetTime) {
        this.jetTime = jetTime;
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

    public List<MasterCtrlModeEntity> getJetModes() {
        return jetModes;
    }

    public void setJetModes(List<MasterCtrlModeEntity> jetModes) {
        this.jetModes = jetModes;
    }

}
