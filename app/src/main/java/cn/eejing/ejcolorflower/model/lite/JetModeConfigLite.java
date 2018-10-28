package cn.eejing.ejcolorflower.model.lite;

import org.litepal.crud.LitePalSupport;

/**
 * 喷射效果配置实体类【数据库保存】
 */

public class JetModeConfigLite extends LitePalSupport {
    private long groupIdMillis;     // 分组ID-时间戳
    private long jetIdMillis;       // 喷射效果ID-时间戳
    private String jetType;         // 喷射效果类型
    private String direction;       // 方向
    private String gap;             // 间隔时间
    private String duration;        // 持续时间
    private String bigGap;          // 大间隔时间
    private String jetRound;        // 喷射次数
    private String high;            // 高度

    public JetModeConfigLite() {
    }

    public long getGroupIdMillis() {
        return groupIdMillis;
    }

    public void setGroupIdMillis(long groupIdMillis) {
        this.groupIdMillis = groupIdMillis;
    }

    public long getJetIdMillis() {
        return jetIdMillis;
    }

    public void setJetIdMillis(long jetIdMillis) {
        this.jetIdMillis = jetIdMillis;
    }

    public String getJetType() {
        return jetType;
    }

    public void setJetType(String jetType) {
        this.jetType = jetType;
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

    public String getBigGap() {
        return bigGap;
    }

    public void setBigGap(String bigGap) {
        this.bigGap = bigGap;
    }

    public String getJetRound() {
        return jetRound;
    }

    public void setJetRound(String jetRound) {
        this.jetRound = jetRound;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }
}
