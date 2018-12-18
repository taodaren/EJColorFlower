package cn.eejing.colorflower.model.manager;

import java.util.ArrayList;
import java.util.List;

import cn.eejing.colorflower.util.LogUtil;

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

    private List<Integer> mListDevCount;

    @Override
    public void setDevCount(int devCount) {
        super.setDevCount(devCount);
        mListDevCount = new ArrayList<>();
        for (int i = 0; i < devCount; i++) {
            mListDevCount.add(0);
        }
    }

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        LogUtil.i(JET, "updateWithDataOut: 进入跑马灯");

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
        long iMax = (mDevCount + 1) >> 1;// 加一除2
        long outputTime = mGap * (iMax - 1) + mDuration * iMax;
        for (int i = 0; i < mDevCount; i++) {
            // 喷射顺序：第几个开始喷
            long timeOutId;
            if (i < iMax) {
                timeOutId = iMax - 1 - i;
            } else {
                timeOutId = (i - (iMax - 1)) - ((mDevCount + 1) % 2);
            }
            int index = mListDevCount.get(i);
            // 判断是否需要停止进料
            if (isLastEffect && mLoopId == mLoop
                    && index < STOP_FEED_ORDER_NUM
                    && (mCurrentTime > (mGap + mDuration) * timeOutId
                    && mCurrentTime <= (mGap + mDuration) * timeOutId + mDuration)) {
                LogUtil.d(JET, "添加停止进料命令");
                dataOut[i] = (byte) STOP_FEED_START;
                index++;
                // 用指定的元素替换此列表中指定位置的元素
                mListDevCount.set(i, index);
            } else {
                if (mCurrentTime > outputTime) {
                    dataOut[i] = 0;
                } else if (mCurrentTime > (mGap + mDuration) * timeOutId
                        && mCurrentTime <= (mGap + mDuration) * timeOutId + mDuration) {
                    dataOut[i] = mHigh;
                } else {
                    dataOut[i] = 0;
                }
            }
        }
        if (mCurrentTime > (outputTime + mGapBig)) {
            mLoopId++;
            mCurrentTime = 0;
        }
        return outputTime;
    }

    private long endsToMiddle(byte[] dataOut) {
        long iMax = (mDevCount + 1) >> 1;
        long outputTime = mGap * (iMax - 1) + mDuration * iMax;
        for (int i = 0; i < mDevCount; i++) {
            int index = mListDevCount.get(i);
            // 判断是否需要停止进料
            if (isLastEffect && mLoopId == mLoop
                    && index < STOP_FEED_ORDER_NUM
                    && ((mCurrentTime > (mGap + mDuration) * i
                    && mCurrentTime <= (mGap + mDuration) * i + mDuration)
                    || (mCurrentTime > (mGap + mDuration) * (mDevCount - i - 1)
                    && mCurrentTime <= (mGap + mDuration) * (mDevCount - i - 1) + mDuration))) {
                LogUtil.d(JET, "添加停止进料命令");
                dataOut[i] = (byte) STOP_FEED_START;
                index++;
                // 用指定的元素替换此列表中指定位置的元素
                mListDevCount.set(i, index);
            } else {
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
        }
        if (mCurrentTime > (outputTime + mGapBig)) {
            mLoopId++;
            mCurrentTime = 0;
        }
        return outputTime;
    }

    private long rightToLeft(byte[] dataOut) {
        long outputTime = mGap * (mDevCount - 1) + mDuration * mDevCount;
        for (int i = 0; i < mDevCount; i++) {
            int index = mListDevCount.get(i);
            // 判断是否需要停止进料
            if (isLastEffect && mLoopId == mLoop
                    && (mCurrentTime > (mGap + mDuration) * (mDevCount - i - 1)
                    && mCurrentTime <= ((mGap + mDuration) * (mDevCount - i - 1) + mDuration)
                    && ((mGap + mDuration) * (mDevCount - i - 1) + mDuration) - mCurrentTime < LAST_TO_END_TIME)
                    && index < STOP_FEED_ORDER_NUM) {
                LogUtil.d(JET, "添加停止进料命令");
                dataOut[i] = (byte) STOP_FEED_START;
                index++;
                // 用指定的元素替换此列表中指定位置的元素
                mListDevCount.set(i, index);
            } else {
                dataOut[i] = (mCurrentTime <= (mGap + mDuration) * (mDevCount - i - 1)
                        || (mCurrentTime > (mGap + mDuration) * (mDevCount - i - 1) + mDuration)
                        || mCurrentTime > outputTime) ? 0 : mHigh;
            }
        }
        if (mCurrentTime > (outputTime + mGapBig)) {
            mLoopId++;
            mCurrentTime = 0;
        }
        return outputTime;
    }

    private long leftToRight(byte[] dataOut) {
        long outputTime = mGap * (mDevCount - 1) + mDuration * mDevCount;
        for (int i = 0; i < mDevCount; i++) {
            int index = mListDevCount.get(i);
            // 判断是否需要停止进料
            if (isLastEffect && mLoopId == mLoop
                    && (mCurrentTime > (mGap + mDuration) * i
                    && mCurrentTime <= ((mGap + mDuration) * i + mDuration)
                    && ((mGap + mDuration) * i + mDuration) - mCurrentTime < LAST_TO_END_TIME)
                    && index < STOP_FEED_ORDER_NUM) {
                LogUtil.d(JET, "添加停止进料命令");
                dataOut[i] = (byte) STOP_FEED_START;
                index++;
                // 用指定的元素替换此列表中指定位置的元素
                mListDevCount.set(i,index);
            } else {
                dataOut[i] = (mCurrentTime <= (mGap + mDuration) * i
                        || (mCurrentTime > (mGap + mDuration) * i + mDuration)
                        || mCurrentTime > outputTime) ? 0 : mHigh;
            }
        }
        if (mCurrentTime > (outputTime + mGapBig)) {
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
