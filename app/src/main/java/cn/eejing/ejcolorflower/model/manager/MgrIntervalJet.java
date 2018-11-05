package cn.eejing.ejcolorflower.model.manager;

import cn.eejing.ejcolorflower.util.LogUtil;

/**
 * 间隔高低管理
 */

public class MgrIntervalJet extends MgrOutputJet {
    private static final String JET = "主控0.1秒";

    private int mDuration;       // 持续时间
    private int mGapBig;         // 大间隔时间

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        LogUtil.i(JET, "updateWithDataOut: 老子进入间隔高低了");

        mCurrentTime++;
        long outputTime = mDuration;
        for (int i = 0; i < mDevCount; i++) {
            if (mCurrentTime <= outputTime) {
                if (mLoopId % 2 == 0) {
                    dataOut[i] = (byte) ((i % 2 == 0) ? 100 : 60);
                } else {
                    dataOut[i] = (byte) ((i % 2 == 0) ? 60 : 100);
                }
            } else {
                dataOut[i] = 0;
            }
        }

        if (mCurrentTime >= (outputTime + mGapBig)) {
            mLoopId++;
            mCurrentTime = 0;
        }

        LogUtil.i(JET, "update over mCurrentTime: " + mCurrentTime);
        LogUtil.i(JET, "update over outputTime: " + outputTime);
        LogUtil.i(JET, "update over mLoopId: " + mLoopId);
        LogUtil.i(JET, "update over mLoop: " + mLoop);
        // 等最后一次循环完毕
        return mCurrentTime > outputTime && mLoopId >= mLoop;
    }

    public void setGapBig(int gapBig) {
        mGapBig = gapBig;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getDuration() {
        return mDuration;
    }

    public int getGapBig() {
        return mGapBig;
    }
}
