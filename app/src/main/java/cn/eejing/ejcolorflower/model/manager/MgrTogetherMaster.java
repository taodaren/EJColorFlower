package cn.eejing.ejcolorflower.model.manager;

/**
 * 齐喷管理
 */

public class MgrTogetherMaster extends MasterOutputManager {
    private int duration;       // 持续时间
    private byte high;          // 高度

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        this.currentTime++;
        long outputTime = this.duration;
        for (int i = 0; i < this.devCount; i++) {
            if (this.currentTime <= outputTime) {
                dataOut[i] = high;
            } else {
                dataOut[i] = 0;
            }
        }

        return this.currentTime > outputTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setHigh(byte high) {
        this.high = high;
    }

    public int getDuration() {
        return duration;
    }

    public byte getHigh() {
        return high;
    }
}
