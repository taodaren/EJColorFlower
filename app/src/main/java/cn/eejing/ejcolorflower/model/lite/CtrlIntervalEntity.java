package cn.eejing.ejcolorflower.model.lite;

/**
 * 间隔高低控制
 */

public class CtrlIntervalEntity extends MasterCtrlModeEntity {
    private String configType;      // 效果功能（方式）
    private String gap;             // 间隔时间
    private String duration;        // 持续时间
    private String high;            // 高度
    private String frequency;       // 次数（换向）
    private int groupId;            // 分组 ID
    private long millis;            // 时间戳

    public CtrlIntervalEntity() {
        super();
    }

    public CtrlIntervalEntity(String type) {
        super(type);
    }

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

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        currentTime++;
        long outputTime = Long.parseLong(duration);
        for (int i = 0; i < devCount; i++) {
            if (currentTime <= outputTime) {
                if (loopId % 2 == 0) {
                    dataOut[i] = (byte) ((i % 2 == 0) ? 100 : 60);
                } else {
                    dataOut[i] = (byte) ((i % 2 == 0) ? 60 : 100);
                }
            } else {
                dataOut[i] = 0;
            }
        }

        if (currentTime >= (outputTime + Integer.parseInt(gap))) {
            loopId++;
            currentTime = 0;
        }

        // 等最后一次循环完毕
        return currentTime > outputTime && loopId >= Integer.parseInt(frequency);
    }
}
