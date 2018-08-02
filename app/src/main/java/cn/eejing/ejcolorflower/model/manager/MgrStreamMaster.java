package cn.eejing.ejcolorflower.model.manager;

/**
 * 流水灯管理
 */

public class MgrStreamMaster extends MasterOutputManager {
    private int direction;      // 方向
    private int gap;            // 间隔时间
    private int duration;       // 持续时间
    private int gapBig;         // 大间隔时间
    private byte high;          // 高度

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        this.currentTime++;
        // 一次运行时间
        long outputTime = 0;
        switch (this.direction) {
            case 0:
                // 从左到右
                outputTime = leftToRight(dataOut);
                break;
            case 1:
                // 从右到左
                outputTime = rightToLeft(dataOut);
                break;
            case 2:
                // 从两端到中间
                outputTime = endsToMiddle(dataOut);
                break;
            case 3:
                // 从中间到两端
                outputTime = middleToEnds(dataOut);
                break;
            default:
                break;
        }

        // 等最后一次循环完毕
        return this.currentTime > outputTime && this.loopId >= this.loop;
    }

    private long middleToEnds(byte[] dataOut) {
        long iMax;
        long outputTime;
        iMax = (this.devCount + 1) >> 1;
        outputTime = this.gap * (iMax - 1) + this.duration;
        for (int i = 0; i < this.devCount; i++) {
            long timeOutId;
            if (i < iMax) {
                timeOutId = iMax - 1 - i;
            } else {
                timeOutId = (i - (iMax - 1)) - ((this.devCount + 1) % 2);
            }
            if (this.currentTime > outputTime) {
                dataOut[i] = 0;
            } else if (this.currentTime > this.gap * timeOutId) {
                dataOut[i] = this.high;
            } else {
                dataOut[i] = 0;
            }
        }
        if (this.currentTime >= (outputTime + this.gapBig)) {
            this.loopId++;
            this.currentTime = 0;
        }
        return outputTime;
    }

    private long endsToMiddle(byte[] dataOut) {
        long iMax;
        long outputTime;
        iMax = (this.devCount + 1) >> 1;
        outputTime = this.gap * (iMax - 1) + this.duration;
        for (int i = 0; i < this.devCount; i++) {
            if (this.currentTime > outputTime) {
                dataOut[i] = 0;
            } else if (this.currentTime > this.gap * i) {
                dataOut[i] = this.high;
            } else if (this.currentTime > this.gap * (this.devCount - i - 1)) {
                dataOut[i] = this.high;
            } else {
                dataOut[i] = 0;
            }
        }
        if (this.currentTime >= (outputTime + this.gapBig)) {
            this.loopId++;
            this.currentTime = 0;
        }
        return outputTime;
    }

    private long rightToLeft(byte[] dataOut) {
        long outputTime;
        outputTime = this.gap * (this.devCount - 1) + this.duration;
        for (int i = 0; i < this.devCount; i++) {
            dataOut[i] = (this.currentTime <= this.gap * (this.devCount - i - 1) || this.currentTime > outputTime) ? 0 : this.high;
        }
        if (this.currentTime >= (outputTime + this.gapBig)) {
            this.loopId++;
            this.currentTime = 0;
        }
        return outputTime;
    }

    private long leftToRight(byte[] dataOut) {
        long outputTime;
        outputTime = this.gap * (this.devCount - 1) + this.duration;
        for (int i = 0; i < this.devCount; i++) {
            dataOut[i] = (this.currentTime <= this.gap * i || this.currentTime > outputTime) ? 0 : this.high;
        }
        if (this.currentTime >= (outputTime + this.gapBig)) {
            this.loopId++;
            this.currentTime = 0;
        }
        return outputTime;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setGapBig(int gapBig) {
        this.gapBig = gapBig;
    }

    public void setHigh(byte high) {
        this.high = high;
    }

    public int getDirection() {
        return direction;
    }

    public int getGap() {
        return gap;
    }

    public int getDuration() {
        return duration;
    }

    public int getGapBig() {
        return gapBig;
    }

    public byte getHigh() {
        return high;
    }
}
