package cn.eejing.ejcolorflower.model.manager;

import android.util.Log;

/**
 * 齐喷管理
 */

public class MgrTogetherMaster extends MasterOutputManager {
    private int duration;       // 持续时间
    private byte high;          // 高度

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        Log.i("CMCML", "updateWithDataOut: 老子进入间隔高低了");

        this.currentTime++;
        long outputTime = this.duration;
        for (int i = 0; i < this.devCount; i++) {
            if (this.currentTime <= outputTime) {
                dataOut[i] = high;
            } else {
                dataOut[i] = 0;
            }
        }

        Log.i("CMCML", "update over currentTime: " + currentTime);
        Log.i("CMCML", "update over outputTime: " + outputTime);
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
