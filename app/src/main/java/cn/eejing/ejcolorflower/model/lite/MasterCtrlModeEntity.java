package cn.eejing.ejcolorflower.model.lite;

import org.litepal.crud.LitePalSupport;

/**
 * 主控效果实体
 */

public class MasterCtrlModeEntity extends LitePalSupport {
    private String devId;        // 主控设备 ID
    private String type;         // 喷射效果
    private long millis;         // 时间戳

    public MasterCtrlModeEntity(String type, long millis) {
        this.type = type;
        this.millis = millis;
    }

    public MasterCtrlModeEntity(String type) {
        this.type = type;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
}
