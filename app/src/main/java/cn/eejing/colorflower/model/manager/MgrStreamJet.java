package cn.eejing.colorflower.model.manager;

import cn.eejing.colorflower.util.LogUtil;

/**
 * 流水灯管理
 */

public class MgrStreamJet extends MgrOutputJet {
    private static final String JET = "主控0.1秒";

    private int mDirection;      // 方向
    private int mGap;            // 间隔时间
    private int mDuration;       // 持续时间
    private int mGapBig;         // 大间隔时间
    private byte mHigh;          // 高度

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        LogUtil.i(JET, "updateWithDataOut: 进入流水灯");
        mCurrentTime++;
        // 一次运行时间
        long outputTime = 0;
        switch (mDirection) {
            case 1:
                // 从左到右
                outputTime = leftToRight(dataOut);
                break;
            case 3:
                // 从右到左
                outputTime = rightToLeft(dataOut);
                break;
            case 2:
                // 从两端到中间
                outputTime = endsToMiddle(dataOut);
                break;
            case 4:
                // 从中间到两端
                outputTime = middleToEnds(dataOut);
                break;
            default:
                break;
        }

        // 等最后一次循环完毕
        return mCurrentTime > outputTime && mLoopId >= mLoop;
    }

    private long middleToEnds(byte[] dataOut) {
        long iMax = (mDevCount + 1) >> 1;
        long outputTime = mGap * (iMax - 1) + mDuration;
        // 判断是否需要停止进料
        if (isLastEffect && mLoopId == mLoop && outputTime - mCurrentTime < LAST_TO_END_TIME && indexNum < STOP_FEED_ORDER_NUM) {
            for (int i = 0; i < mDevCount; i++) {
                dataOut[i] = (byte) STOP_FEED_START;
            }
            indexNum++;
        } else {
            for (int i = 0; i < mDevCount; i++) {
                long timeOutId;
                if (i < iMax) {
                    timeOutId = iMax - 1 - i;
                } else {
                    timeOutId = (i - (iMax - 1)) - ((mDevCount + 1) % 2);
                }
                if (mCurrentTime > outputTime) {
                    dataOut[i] = 0;
                } else if (mCurrentTime > mGap * timeOutId) {
                    dataOut[i] = mHigh;
                } else {
                    dataOut[i] = 0;
                }
            }
        }
        if (mCurrentTime >= (outputTime + mGapBig)) {
            mLoopId++;
            mCurrentTime = 0;
        }
        return outputTime;
    }

    private long endsToMiddle(byte[] dataOut) {
        long iMax = (mDevCount + 1) >> 1;
        long outputTime = mGap * (iMax - 1) + mDuration;
        // 判断是否需要停止进料
        if (isLastEffect && mLoopId == mLoop && outputTime - mCurrentTime < LAST_TO_END_TIME && indexNum < STOP_FEED_ORDER_NUM) {
            for (int i = 0; i < mDevCount; i++) {
                dataOut[i] = (byte) STOP_FEED_START;
            }
            indexNum++;
        } else {
            for (int i = 0; i < mDevCount; i++) {
                if (mCurrentTime > outputTime) {
                    dataOut[i] = 0;
                } else if (mCurrentTime > mGap * i) {
                    dataOut[i] = mHigh;
                } else if (mCurrentTime > mGap * (mDevCount - i - 1)) {
                    dataOut[i] = mHigh;
                } else {
                    dataOut[i] = 0;
                }
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
        outputTime = mGap * (mDevCount - 1) + mDuration;
        // 判断是否需要停止进料
        if (isLastEffect && mLoopId == mLoop && outputTime - mCurrentTime < LAST_TO_END_TIME && indexNum < STOP_FEED_ORDER_NUM) {
            LogUtil.d(JET, "添加停止进料命令");
            for (int i = 0; i < mDevCount; i++) {
                dataOut[i] = (byte) STOP_FEED_START;
            }
            indexNum++;
        } else {
            for (int i = 0; i < mDevCount; i++) {
                dataOut[i] = (mCurrentTime <= mGap * (mDevCount - i - 1) || mCurrentTime > outputTime) ? 0 : mHigh;
            }
        }
        if (mCurrentTime >= (outputTime + mGapBig)) {
            mLoopId++;
            mCurrentTime = 0;
        }
        return outputTime;
    }

    private long leftToRight(byte[] dataOut) {
        long outputTime;
        outputTime = mGap * (mDevCount - 1) + mDuration;
        // 判断是否需要停止进料
        if (isLastEffect && mLoopId == mLoop && outputTime - mCurrentTime < LAST_TO_END_TIME && indexNum < STOP_FEED_ORDER_NUM) {
            LogUtil.d(JET, "添加停止进料命令");
            for (int i = 0; i < mDevCount; i++) {
                dataOut[i] = (byte) STOP_FEED_START;
            }
            indexNum++;
        } else {
            for (int i = 0; i < mDevCount; i++) {
                dataOut[i] = (mCurrentTime <= mGap * i || mCurrentTime > outputTime) ? 0 : mHigh;
            }
        }
        if (mCurrentTime >= (outputTime + mGapBig)) {
            // 设置为大于 防止 mGapBig==0 一直循环停不了 mGapBig 将多 0.1s
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
}
