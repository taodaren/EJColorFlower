package cn.eejing.ejcolorflower.model.manager;

import android.util.Log;

/**
 * 齐喷管理
 */

public class MgrTogetherJet extends MgrOutputJet {
    private int mDuration;       // 持续时间
    private byte mHigh;          // 高度

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        Log.i("CMCML", "updateWithDataOut: 老子进入齐喷了");

        mCurrentTime++;
        long outputTime = mDuration;
        for (int i = 0; i < mDevCount; i++) {
            if (mCurrentTime <= outputTime) {
                dataOut[i] = mHigh;
            } else {
                dataOut[i] = 0;
            }
        }

        Log.i("CMCML", "update over mCurrentTime: " + mCurrentTime);
        Log.i("CMCML", "update over outputTime: " + outputTime);
        return mCurrentTime > outputTime;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setHigh(byte high) {
        mHigh = high;
    }

    public int getDuration() {
        return mDuration;
    }

    public byte getHigh() {
        return mHigh;
    }
}
