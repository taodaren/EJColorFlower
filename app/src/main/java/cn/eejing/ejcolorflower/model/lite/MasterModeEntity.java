package cn.eejing.ejcolorflower.model.lite;

import org.litepal.crud.LitePalSupport;

public class MasterModeEntity extends LitePalSupport {
    private String mode;

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
}
