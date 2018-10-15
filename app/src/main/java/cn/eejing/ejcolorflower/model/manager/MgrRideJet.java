package cn.eejing.ejcolorflower.model.manager;

import android.util.Log;

/**
 * 跑马灯管理
 */

public class MgrRideJet extends MgrOutputJet {
    private static final String JET = "主控0.1秒";

    private int mDirection;      // 方向
    private int mGap;            // 间隔时间
    private int mDuration;       // 持续时间
    private int mGapBig;         // 大间隔时间
    private byte mHigh;          // 高度

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        Log.i(JET, "updateWithDataOut: 老子进入跑马灯了");

        mCurrentTime++;
        // 一次运行时间
        long outputTime = 0;
        switch (mDirection) {
            case 1:
                // 从左到右
                Log.i(JET, "进入从左到右");
                outputTime = leftToRight(dataOut);
                Log.i(JET, "从左到右 outputTime: " + outputTime);
                break;
            case 3:
                // 从右到左
                Log.i(JET, "进入从右到左");
                outputTime = rightToLeft(dataOut);
                Log.i(JET, "从右到左 outputTime: " + outputTime);
                break;
            case 2:
                // 从两端到中间
                Log.i(JET, "进入从两端到中间");
                outputTime = endsToMiddle(dataOut);
                Log.i(JET, "从两端到中间 outputTime: " + outputTime);
                break;
            case 4:
                // 从中间到两端
                Log.i(JET, "进入从中间到两端");
                outputTime = middleToEnds(dataOut);
                Log.i(JET, "从中间到两端 outputTime: " + outputTime);
                break;
            default:
                break;
        }

        Log.i(JET, "update over mCurrentTime: " + mCurrentTime);
        Log.i(JET, "update over outputTime: " + outputTime);
        Log.i(JET, "update over mLoopId: " + mLoopId);
        Log.i(JET, "update over mLoop: " + mLoop);
        // 等最后一次循环完毕
        return mCurrentTime > outputTime && mLoopId >= mLoop;
    }

    private long middleToEnds(byte[] dataOut) {
        long iMax;
        long outputTime;
        iMax = (mDevCount + 1) >> 1;//加一除2
        outputTime = mGap * (iMax - 1) + mDuration * iMax;
        for (int i = 0; i < mDevCount; i++) {
            // 喷射顺序：第几个开始喷
            long timeOutId;
            if (i < iMax) {
                timeOutId = iMax - 1 - i;
            } else {
                timeOutId = (i - (iMax - 1)) - ((mDevCount + 1) % 2);
            }
            if (mCurrentTime > outputTime) {
                dataOut[i] = 0;
            } else if (mCurrentTime > (mGap + mDuration) * timeOutId && mCurrentTime <= (mGap + mDuration) * timeOutId + mDuration) {
                dataOut[i] = mHigh;
            } else {
                dataOut[i] = 0;
            }
        }
        if (mCurrentTime >= (outputTime + mGapBig)) {
            mLoopId++;
            mCurrentTime = 0;
        }
        return outputTime;
    }

    private long endsToMiddle(byte[] dataOut) {
        long iMax;
        long outputTime;
        iMax = (mDevCount + 1) >> 1;
        outputTime = mGap * (iMax - 1) + mDuration * iMax;
        for (int i = 0; i < mDevCount; i++) {

            if (mCurrentTime > outputTime) {
                dataOut[i] = 0;
            } else if (mCurrentTime > (mGap + mDuration) * i
                    && mCurrentTime <= (mGap + mDuration) * i + mDuration) {
                dataOut[i] = mHigh;
            } else if (mCurrentTime > (mGap + mDuration) * (mDevCount - i - 1)
                    && mCurrentTime <= (mGap + mDuration) * (mDevCount - i - 1) + mDuration) {
                dataOut[i] = mHigh;
            } else {
                dataOut[i] = 0;
            }
        }
        if (mCurrentTime >= (outputTime + mGapBig)) {
            mLoopId++;
            mCurrentTime = 0;
        }
        return outputTime;
    }

    private long rightToLeft(byte[] dataOut) {
        long outputTime;
        outputTime = mGap * (mDevCount - 1) + mDuration * mDevCount;
        for (int i = 0; i < mDevCount; i++) {
            dataOut[i] = (mCurrentTime <= (mGap + mDuration) * (mDevCount - i - 1)
                    || (mCurrentTime > (mGap + mDuration) * (mDevCount - i - 1) + mDuration)
                    || mCurrentTime > outputTime) ? 0 : mHigh;
        }
        if (mCurrentTime >= (outputTime + mGapBig)) {
            mLoopId++;
            mCurrentTime = 0;
        }
        return outputTime;
    }

    private long leftToRight(byte[] dataOut) {
        long outputTime;
        outputTime = mGap * (mDevCount - 1) + mDuration * mDevCount;
        for (int i = 0; i < mDevCount; i++) {
            dataOut[i] = (mCurrentTime <= (mGap + mDuration) * i || (mCurrentTime > (mGap + mDuration) * i + mDuration) || mCurrentTime > outputTime) ? 0 : mHigh;
        }
        if (mCurrentTime >= (outputTime + mGapBig)) {
            mLoopId++;
            mCurrentTime = 0;
        }
        return outputTime;
    }

    public void setDirection(int direction) {
        mDirection = direction;
    }

    public void setGap(int gap) {
        mGap = gap;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setGapBig(int gapBig) {
        mGapBig = gapBig;
    }

    public void setHigh(byte high) {
        mHigh = high;
    }

    public int getDirection() {
        return mDirection;
    }

    public int getGap() {
        return mGap;
    }

    public int getDuration() {
        return mDuration;
    }

    public int getGapBig() {
        return mGapBig;
    }

    public byte getHigh() {
        return mHigh;
    }
}
