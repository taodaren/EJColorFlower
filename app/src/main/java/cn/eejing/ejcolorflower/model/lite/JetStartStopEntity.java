package cn.eejing.ejcolorflower.model.lite;

import org.litepal.crud.LitePalSupport;

/**
 * 喷射状态
 */

public class JetStartStopEntity extends LitePalSupport {
    private int groupId;
    private String status;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
