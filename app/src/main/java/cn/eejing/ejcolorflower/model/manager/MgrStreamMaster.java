package cn.eejing.ejcolorflower.model.manager;

import android.util.Log;

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
        Log.i("CMCML", "updateWithDataOut: 老子进入流水灯了");
        this.currentTime++;
        // 一次运行时间
        long outputTime = 0;
        switch (this.direction) {
            case 1:
                // 从左到右
                Log.i("CMCML", "进入从左到右");
                outputTime = leftToRight(dataOut);
                Log.i("CMCML", "从左到右 outputTime: " + outputTime);
                break;
            case 3:
                // 从右到左
                Log.i("CMCML", "进入从右到左");
                outputTime = rightToLeft(dataOut);
                Log.i("CMCML", "从右到左 outputTime: " + outputTime);
                break;
            case 2:
                // 从两端到中间
                Log.i("CMCML", "进入从两端到中间");
                outputTime = endsToMiddle(dataOut);
                Log.i("CMCML", "从两端到中间 outputTime: " + outputTime);
                break;
            case 4:
                // 从中间到两端
                Log.i("CMCML", "进入从中间到两端");
                outputTime = middleToEnds(dataOut);
                Log.i("CMCML", "从中间到两端 outputTime: " + outputTime);
                break;
            default:
                break;
        }

        Log.i("CMCML", "update over currentTime: " + currentTime);
        Log.i("CMCML", "update over outputTime: " + outputTime);
        Log.i("CMCML", "update over loopId: " + loopId);
        Log.i("CMCML", "update over loop: " + loop);

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
