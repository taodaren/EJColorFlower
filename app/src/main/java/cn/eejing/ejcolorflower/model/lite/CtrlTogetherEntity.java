package cn.eejing.ejcolorflower.model.lite;

import org.litepal.crud.LitePalSupport;

public class CtrlTogetherEntity extends LitePalSupport {
    private String configType;      // 效果功能（方式）
    private String duration;        // 持续时间
    private String high;            // 高度
    private int groupId;            // 分组 ID

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

}
