package cn.eejing.colorflower.model.manager;

import cn.eejing.colorflower.util.LogUtil;

/**
 * 间隔高低管理
 */

public class MgrIntervalJet extends MgrOutputJet {
    private static final String JET = "主控0.1秒";

    private int mDuration;       // 持续时间
    private int mGapBig;         // 大间隔时间
    private int mHighMax;        // 最高高度
    private int mHighMin;        // 最低高度

    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        LogUtil.i(JET, "updateWithDataOut: 进入间隔高低");

        mCurrentTime++;
        long outputTime = mDuration;
        // 判断是否需要停止进料
        if (isLastEffect && mLoopId == mLoop
                && (outputTime - mCurrentTime) < LAST_TO_END_TIME
                && indexNum < STOP_FEED_ORDER_NUM) {
            LogUtil.d(JET, "添加停止进料命令");
            for (int i = 0; i < mDevCount; i++) {
                // 设置为大于 防止bigIntervalTime==0一直循环停不了 bigIntervalTime将多0.1秒
                if (mCurrentTime <= outputTime) {
                    dataOut[i] = (byte) STOP_FEED_START;
                } else {
                    dataOut[i] = 0;
                }
            }
            indexNum++;
        } else {
            for (int i = 0; i < mDevCount; i++) {
                // 设置为大于 防止bigIntervalTime==0一直循环停不了 bigIntervalTime将多0.1秒
                if (mCurrentTime <= outputTime) {
                    if (mLoopId % 2 == 0) {
                        dataOut[i] = (byte) ((i % 2 == 0) ? mHighMax : mHighMin);
                    } else {
                        dataOut[i] = (byte) ((i % 2 == 0) ? mHighMin : mHighMax);
                    }

                } else {
                    dataOut[i] = 0;
                }
            }
        }

        if (mCurrentTime > (outputTime + mGapBig)) {
            mLoopId++;
            mCurrentTime = 0;
        }

        // 等最后一次循环完毕
        return mCurrentTime >= outputTime && mLoopId >= mLoop;
    }

    public void setGapBig(int gapBig) {
        mGapBig = gapBig;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setHighMax(int mHighMax) {
        this.mHighMax = mHighMax;
    }

    public void setHighMin(int mHighMin) {
        this.mHighMin = mHighMin;
    }
}
