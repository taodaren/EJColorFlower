package cn.eejing.ejcolorflower.model.request;

import java.util.List;

import cn.eejing.ejcolorflower.model.lite.MasterCtrlSetEntity;

/**
 * 分组信息实体类
 */

public class MasterGroupListBean {
    private int isSelected;                     // 是否选中 1-选中 0-未选中
    private String groupName;                   // 分组名称
    private List<MasterCtrlSetEntity> cfgInfo;  // 设备数量、起始DMX、喷射效果

    public MasterGroupListBean(int isSelected, String groupName, List<MasterCtrlSetEntity> cfgInfo) {
        this.isSelected = isSelected;
        this.groupName = groupName;
        this.cfgInfo = cfgInfo;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<MasterCtrlSetEntity> getCfgInfo() {
        return cfgInfo;
    }

    public void setCfgInfo(List<MasterCtrlSetEntity> cfgInfo) {
        this.cfgInfo = cfgInfo;
    }
}
