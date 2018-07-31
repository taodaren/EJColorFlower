package cn.eejing.ejcolorflower.model.device;

/**
 * 流水灯管理
 */

public class MasterOutputStreamManager extends MasterOutputManager {
    private int direction;
    private int intervalTime;       // 单位 0.1s
    private int durationTime;       // 单位 0.1s
    private int bigIntervalTime;    // 单位 0.1s
    private byte outHigh;

    /**
     * 需要在界面点击开始按钮后，开启的定时器中被调用
     *
     * @param dataOut 用于生成每个设备喷射高度值，放在 dataOut 中
     * @return 当前组是否完成喷射(false 表示继续, true 表示已经完成)
     */
    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        this.currentTime++;
        // 一次运行时间
        long outputTime = 0;
        long iMax;

        switch (this.direction) {
            // 从左到右
            case 0:
                outputTime = this.intervalTime * (this.deviceCount - 1) + this.durationTime;
                for (int i = 0; i < this.deviceCount; i++) {
                    dataOut[i] = (this.currentTime <= this.intervalTime * i || this.currentTime > outputTime) ? 0 : this.outHigh;
                }
                if (this.currentTime >= (outputTime + this.bigIntervalTime)) {
                    this.loopId++;
                    this.currentTime = 0;
                }
                break;
            // 从右到左
            case 1:
                outputTime = this.intervalTime * (this.deviceCount - 1) + this.durationTime;
                for (int i = 0; i < this.deviceCount; i++) {
                    dataOut[i] = (this.currentTime <= this.intervalTime * (this.deviceCount - i - 1) || this.currentTime > outputTime) ? 0 : this.outHigh;
                }
                if (this.currentTime >= (outputTime + this.bigIntervalTime)) {
                    this.loopId++;
                    this.currentTime = 0;
                }
                break;
            // 从两端到中间
            case 2:
                iMax = (this.deviceCount + 1) >> 1;
                outputTime = this.intervalTime * (iMax - 1) + this.durationTime;
                for (int i = 0; i < this.deviceCount; i++) {
                    if (this.currentTime > outputTime) {
                        dataOut[i] = 0;
                    } else if (this.currentTime > this.intervalTime * i) {
                        dataOut[i] = this.outHigh;
                    } else if (this.currentTime > this.intervalTime * (this.deviceCount - i - 1)) {
                        dataOut[i] = this.outHigh;
                    } else {
                        dataOut[i] = 0;
                    }
                }
                if (this.currentTime >= (outputTime + this.bigIntervalTime)) {
                    this.loopId++;
                    this.currentTime = 0;
                }
                break;
            // 从中间到两端
            case 3:
                iMax = (this.deviceCount + 1) >> 1;
                outputTime = this.intervalTime * (iMax - 1) + this.durationTime;
                for (int i = 0; i < this.deviceCount; i++) {
                    long timeOutId;
                    if (i < iMax) {
                        timeOutId = iMax - 1 - i;
                    } else {
                        timeOutId = (i - (iMax - 1)) - ((this.deviceCount + 1) % 2);
                    }
                    if (this.currentTime > outputTime) {
                        dataOut[i] = 0;
                    } else if (this.currentTime > this.intervalTime * timeOutId) {
                        dataOut[i] = this.outHigh;
                    } else {
                        dataOut[i] = 0;
                    }
                }
                if (this.currentTime >= (outputTime + this.bigIntervalTime)) {
                    this.loopId++;
                    this.currentTime = 0;
                }
                break;
            default:
                break;
        }

        // 等最后一次循环完毕
        return this.currentTime > outputTime && this.loopId >= this.loopCount;
    }
}
