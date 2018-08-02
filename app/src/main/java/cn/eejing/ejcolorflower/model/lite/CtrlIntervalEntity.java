package cn.eejing.ejcolorflower.model.lite;

import org.litepal.crud.LitePalSupport;

/**
 * 间隔高低控制
 */

public class CtrlIntervalEntity extends LitePalSupport {
    private String configType;      // 效果功能（方式）
    private String gap;             // 间隔时间
    private String duration;        // 持续时间
    private String high;            // 高度
    private String frequency;       // 次数（换向）
    private int groupId;            // 分组 ID
    private long millis;            // 时间戳

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
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

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
}
