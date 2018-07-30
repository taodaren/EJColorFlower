package cn.eejing.ejcolorflower.model.lite;

import org.litepal.crud.LitePalSupport;

public class MasterModeEntity extends LitePalSupport {
    private String mode;// 类型
    private long millis;// 时间戳

    public MasterModeEntity() {
    }

    public MasterModeEntity(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
}
