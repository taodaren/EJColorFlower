package cn.eejing.ejcolorflower.model.device;

public class DeviceMaterialStatus {
    private int exist;
    private long userId;
    private long materialId;

    public DeviceMaterialStatus() {
    }

    public DeviceMaterialStatus(int exist, long userId, long materialId) {
        this.exist = exist;
        this.userId = userId;
        this.materialId = materialId;
    }

    public int getExist() {
        return exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(long materialId) {
        this.materialId = materialId;
    }
}
