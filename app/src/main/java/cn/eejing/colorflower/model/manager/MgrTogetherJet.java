package cn.eejing.colorflower.model.manager;

import cn.eejing.colorflower.util.LogUtil;

/**
 * 齐喷管理
 */

public class MgrTogetherJet extends MgrOutputJet {
    private static final String JET = "主控0.1秒";

    private int mDuration;       // 持续时间
    private byte mHigh;          // 高度

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        LogUtil.i(JET, "updateWithDataOut: 老子进入齐喷了");

        mCurrentTime++;
        long outputTime = mDuration;
        for (int i = 0; i < mDevCount; i++) {
            if (mCurrentTime <= outputTime) {
                dataOut[i] = mHigh;
            } else {
                dataOut[i] = 0;
            }
        }

        LogUtil.i(JET, "update over mCurrentTime: " + mCurrentTime);
        LogUtil.i(JET, "update over outputTime: " + outputTime);
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
