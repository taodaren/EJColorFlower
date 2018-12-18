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
        LogUtil.i(JET, "updateWithDataOut: 进入齐喷");

        mCurrentTime++;
        long outputTime = mDuration;
        // 判断是否需要停止进料
        if (isLastEffect
                && ((outputTime - mCurrentTime) < LAST_TO_END_TIME)
                && (indexNum < STOP_FEED_ORDER_NUM)) {
            LogUtil.d(JET, "添加停止进料命令");
            for (int i = 0; i < mDevCount; i++) {
                if (mCurrentTime <= outputTime) {
                    dataOut[i] = (byte) STOP_FEED_START;
                } else {
                    dataOut[i] = 0;
                }
            }
            indexNum++;
        } else {
            for (int i = 0; i < mDevCount; i++) {
                if (mCurrentTime <= outputTime) {
                    dataOut[i] = mHigh;
                } else {
                    dataOut[i] = 0;
                }
            }
        }
        return mCurrentTime >= outputTime;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setHigh(byte high) {
        mHigh = high;
    }

}
