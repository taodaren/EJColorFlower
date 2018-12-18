package cn.eejing.colorflower.model.manager;

import static cn.eejing.colorflower.app.AppConstant.BORDER_TO_CENTER;
import static cn.eejing.colorflower.app.AppConstant.CENTER_TO_BORDER;
import static cn.eejing.colorflower.app.AppConstant.CONFIG_DELAY;
import static cn.eejing.colorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.colorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.colorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.colorflower.app.AppConstant.CONFIG_TOGETHER;
import static cn.eejing.colorflower.app.AppConstant.LEFT_TO_RIGHT;
import static cn.eejing.colorflower.app.AppConstant.RIGHT_TO_LEFT;

/**
 * 主控输出管理
 */

public class MgrOutputJet {
    public static final int STOP_FEED_START     =    129;    // 停止进料开始【清料命令】
    public static final int STOP_FEED_RELEASE   =    130;    // 停止进料解除【停止清料命令】
    public static final int PREPARE_FEED_START  =    133;    // 预进料
    public static final int PREPARE_FEED_END    =    134;    // 停止预进料

    public static final int LAST_TO_END_TIME    =    30;     // 距离喷射结束发送停止进料的时间（单位0.1s）
    public static final int STOP_FEED_ORDER_NUM =    2;      // 发送停止进料的命令次数
    public static final int PRE_FEED_TIME       =    1999;   // 预进料时间（单位毫秒）
    public static final int PRE_FEED_INTERVAL   =    1000;   // 预进料间隔时间（单位毫秒）

    public int mDevCount;           // 设备数量
    public int indexNum;            // 发送停止进料的次数
    public int mLoop;               // 循环次数
    public int mLoopId;             // 当前循环的次数
    public int mCurrentTime;        // 当前时间
    public String mType;            // 喷射效果
    public boolean isLastEffect;    // 判断是否是每组中设置喷射效果的最后一个 用于在喷射结束前停止进料

    public int getDevCount() {
        return mDevCount;
    }

    public void setDevCount(int devCount) {
        this.mDevCount = devCount;
    }

    public void setLoop(int loop) {
        this.mLoop = loop;
    }

    public void setLoopId(int loopId) {
        this.mLoopId = loopId;
    }

    public void setCurrentTime(int currentTime) {
        this.mCurrentTime = currentTime;
    }

    public void setType(String type) {
        this.mType = type;
    }

    /**
     * 需要在界面点击开始按钮后，开启的定时器中被调用
     *
     * @param dataOut 用于生成每个设备喷射高度值，放在 dataOut 中
     * @return 当前组是否完成喷射(false 表示继续, true 表示已经完成)
     */
    public boolean updateWithDataOut(byte[] dataOut) {
        // 默认是完成当前组输出，进入到下一组
        return true;
    }

    /**
     * 计算某个效果的时间
     *
     * @param devNum       设备数量
     * @param type         效果类型
     * @param strDirection 方向
     * @param strGap       间隔时间
     * @param strDuration  持续时间
     * @param strBigGap    大间隔时间
     * @param strJetRound  喷射次数
     * @return 某个效果类型的喷射时间
     */
    public static float calCountAloneTime(int devNum, String type, String strDirection, String strGap, String strDuration, String strBigGap, String strJetRound) {
        float gap, duration, gapBig, onceTime, totalTime = 0;
        int frequency;

        gap = Float.parseFloat(strGap);
        duration = Float.parseFloat(strDuration);
        gapBig = Float.parseFloat(strBigGap);
        frequency = Integer.parseInt(strJetRound);

        switch (type) {
            case CONFIG_STREAM:
                switch (strDirection) {
                    case LEFT_TO_RIGHT:
                    case RIGHT_TO_LEFT:
                        // 从左到右或者从右到左
                        onceTime = (devNum - 1) * gap + duration;
                        totalTime = onceTime * (frequency + 1) + frequency * gapBig;
                        break;
                    case BORDER_TO_CENTER:
                    case CENTER_TO_BORDER:
                        // 从中间到两端或者从两端到中间
                        if (devNum % 2 == 0) {
                            onceTime = (devNum / 2 - 1) * gap + duration;
                            totalTime = onceTime * (frequency + 1) + frequency * gapBig;
                            break;
                        } else {
                            onceTime = (devNum / 2) * gap + duration;
                            totalTime = onceTime * (frequency + 1) + frequency * gapBig;
                            break;
                        }
                }
            case CONFIG_RIDE:
                switch (strDirection) {
                    case LEFT_TO_RIGHT:
                    case RIGHT_TO_LEFT:
                        // 从左到右或者从右到左
                        onceTime = (devNum - 1) * gap + duration * devNum;
                        totalTime = onceTime * (frequency + 1) + frequency * gapBig;
                        break;
                    case BORDER_TO_CENTER:
                    case CENTER_TO_BORDER:
                        // 从中间到两端或者从两端到中间
                        if (devNum % 2 == 0) {
                            onceTime = (devNum / 2 - 1) * gap + duration * (devNum / 2);
                            totalTime = onceTime * (frequency + 1) + frequency * gapBig;
                            break;
                        } else {
                            onceTime = (devNum / 2) * gap + duration * ((devNum + 1) / 2);
                            totalTime = onceTime * (frequency + 1) + frequency * gapBig;
                            break;
                        }
                }
                break;
            case CONFIG_INTERVAL:
                totalTime = duration * (frequency + 1) + gap * (frequency);
                break;
            case CONFIG_TOGETHER:
            case CONFIG_DELAY:
                totalTime = duration;
                break;
        }
        return totalTime;
    }
}
