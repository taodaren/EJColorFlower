package cn.eejing.ejcolorflower.model.lite;

/**
 * 流水灯控制
 */

public class CtrlStreamEntity extends MasterCtrlModeEntity {
    private String configType;      // 效果功能（方式）
    private String direction;       // 方向
    private String gap;             // 间隔时间
    private String duration;        // 持续时间
    private String gapBig;          // 大间隔时间
    private String loop;            // 循环次数
    private String high;            // 高度
    private int groupId;            // 分组 ID
    private long millis;            // 时间戳

    public CtrlStreamEntity() {
        super();
    }

    public CtrlStreamEntity(String type) {
        super(type);
    }

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

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        int niGap = Integer.parseInt(gap);
        int niGapBig = Integer.parseInt(gapBig);
        int niDuration = Integer.parseInt(duration);
        int niHigh = Integer.parseInt(high);
        byte byHigh = (byte) niHigh;

        currentTime++;
        // 一次运行时间
        long outputTime = 0;
        long iMax;

        switch (Integer.parseInt(direction)) {
            // 从左到右
            case 0:
                outputTime = niGap * (devCount - 1) + niDuration;
                for (int i = 0; i < devCount; i++) {
                    dataOut[i] = (currentTime <= niGap * i || currentTime > outputTime) ? 0 : byHigh;
                }
                if (currentTime >= (outputTime + niGapBig)) {
                    loopId++;
                    currentTime = 0;
                }
                break;
            // 从右到左
            case 1:
                outputTime = niGap * (devCount - 1) + niDuration;
                for (int i = 0; i < devCount; i++) {
                    dataOut[i] = (currentTime <= niGap * (devCount - i - 1) || currentTime > outputTime) ? 0 : byHigh;
                }
                if (currentTime >= (outputTime + niGapBig)) {
                    loopId++;
                    currentTime = 0;
                }
                break;
            // 从两端到中间
            case 2:
                iMax = (devCount + 1) >> 1;
                outputTime = niGap * (iMax - 1) + niDuration;
                for (int i = 0; i < devCount; i++) {
                    if (currentTime > outputTime) {
                        dataOut[i] = 0;
                    } else if (currentTime > niGap * i) {
                        dataOut[i] = byHigh;
                    } else if (currentTime > niGap * (devCount - i - 1)) {
                        dataOut[i] = byHigh;
                    } else {
                        dataOut[i] = 0;
                    }
                }
                if (currentTime >= (outputTime + niGapBig)) {
                    loopId++;
                    currentTime = 0;
                }
                break;
            // 从中间到两端
            case 3:
                iMax = (devCount + 1) >> 1;
                outputTime = niGap * (iMax - 1) + niDuration;
                for (int i = 0; i < devCount; i++) {
                    long timeOutId;
                    if (i < iMax) {
                        timeOutId = iMax - 1 - i;
                    } else {
                        timeOutId = (i - (iMax - 1)) - ((devCount + 1) % 2);
                    }
                    if (currentTime > outputTime) {
                        dataOut[i] = 0;
                    } else if (currentTime > niGap * timeOutId) {
                        dataOut[i] = byHigh;
                    } else {
                        dataOut[i] = 0;
                    }
                }
                if (currentTime >= (outputTime + niGapBig)) {
                    loopId++;
                    currentTime = 0;
                }
                break;
            default:
                break;
        }

        // 等最后一次循环完毕
        return currentTime > outputTime && loopId >= Integer.parseInt(loop);
    }
}
