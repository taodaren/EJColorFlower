package cn.eejing.ejcolorflower.model.device;

/**
 * 间隔高低管理
 */

public class MasterOutputIntervalManager extends MasterOutputManager {
    private int bigIntervalTime;    // 单位 0.1s
    private int durationTime;       // 单位 0.1s

    /**
     * 需要在界面点击开始按钮后，开启的定时器中被调用
     *
     * @param dataOut 用于生成每个设备喷射高度值，放在 dataOut 中
     * @return 当前组是否完成喷射(false 表示继续, true 表示已经完成)
     */
    @Override
    public boolean updateWithDataOut(byte[] dataOut) {
        this.currentTime++;
        long outputTime = this.durationTime;
        for (int i = 0; i < this.deviceCount; i++) {
            if (this.currentTime <= outputTime) {
                if (this.loopId % 2 == 0) {
                    dataOut[i] = (byte) ((i % 2 == 0) ? 100 : 60);
                } else {
                    dataOut[i] = (byte) ((i % 2 == 0) ? 60 : 100);
                }
            } else {
                dataOut[i] = 0;
            }
        }

        if (this.currentTime >= (outputTime + this.bigIntervalTime)) {
            this.loopId++;
            this.currentTime = 0;
        }

        // 等最后一次循环完毕
        return this.currentTime > outputTime && this.loopId >= this.loopCount;
    }
}
