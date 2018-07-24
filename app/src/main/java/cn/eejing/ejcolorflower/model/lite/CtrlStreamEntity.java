package cn.eejing.ejcolorflower.model.lite;

import org.litepal.crud.LitePalSupport;

public class CtrlStreamEntity extends LitePalSupport {
    private String configType;      // 效果功能（方式）
    private String direction;       // 方向
    private String gap;             // 间隔时间
    private String duration;        // 持续时间
    private String gapBig;          // 大间隔时间
    private String loop;            // 循环次数
    private String high;            // 高度
    private int groupId;            // 分组 ID

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getGap() {
        return gap;
    }

    public void setGap(String gap) {
        this.gap = gap;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getGapBig() {
        return gapBig;
    }

    public void setGapBig(String gapBig) {
        this.gapBig = gapBig;
    }

    public String getLoop() {
        return loop;
    }

    public void setLoop(String loop) {
        this.loop = loop;
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
