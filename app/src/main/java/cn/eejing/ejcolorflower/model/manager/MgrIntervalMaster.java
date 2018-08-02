package cn.eejing.ejcolorflower.model.manager;

/**
 * 间隔高低管理
 */

public class MgrIntervalMaster extends MasterOutputManager {
    private int duration;       // 持续时间
    private int gapBig;         // 大间隔时间

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        this.currentTime++;
        long outputTime = this.duration;
        for (int i = 0; i < this.devCount; i++) {
            if (this.currentTime <= outputTime) {
                if (this.loopId % 2 == 0) {
                    dataOut[i] = (byte) ((i % 2 == 0) ? 100 : 60);
                } else {
                    dataOut[i] = (byte) ((i % 2 == 0) ? 60 : 100);
                }
            } else {
                dataOut[i] = 0;
            }
        }

        if (this.currentTime >= (outputTime + this.gapBig)) {
            this.loopId++;
            this.currentTime = 0;
        }

        // 等最后一次循环完毕
        return this.currentTime > outputTime && this.loopId >= this.loop;
    }

    public void setGapBig(int gapBig) {
        this.gapBig = gapBig;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public int getGapBig() {
        return gapBig;
    }
}
