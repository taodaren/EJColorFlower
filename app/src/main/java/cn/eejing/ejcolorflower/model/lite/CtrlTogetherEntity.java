package cn.eejing.ejcolorflower.model.lite;

/**
 * 齐喷控制
 */

public class CtrlTogetherEntity extends MasterCtrlModeEntity {
    private String configType;      // 效果功能（方式）
    private String duration;        // 持续时间
    private String high;            // 高度
    private int groupId;            // 分组 ID
    private long millis;            // 时间戳

    public CtrlTogetherEntity() {
        super();
    }

    public CtrlTogetherEntity(String type) {
        super(type);
    }

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

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        currentTime++;
        long outputTime = Long.parseLong(duration);
        for (int i = 0; i < devCount; i++) {
            if (currentTime <= outputTime) {
                dataOut[i] = (byte) Integer.parseInt(high);
            } else {
                dataOut[i] = 0;
            }
        }

        return currentTime > outputTime;
    }
}
