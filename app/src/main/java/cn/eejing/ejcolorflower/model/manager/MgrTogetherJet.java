package cn.eejing.ejcolorflower.model.manager;

import android.util.Log;

/**
 * 齐喷管理
 */

public class MgrTogetherJet extends MgrOutputJet {
    private static final String JET = "主控0.1秒";

    private int mDuration;       // 持续时间
    private byte mHigh;          // 高度

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        Log.i(JET, "updateWithDataOut: 老子进入齐喷了");

        mCurrentTime++;
        long outputTime = mDuration;
        for (int i = 0; i < mDevCount; i++) {
            if (mCurrentTime <= outputTime) {
                dataOut[i] = mHigh;
            } else {
                dataOut[i] = 0;
            }
        }

        Log.i(JET, "update over mCurrentTime: " + mCurrentTime);
        Log.i(JET, "update over outputTime: " + outputTime);
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
