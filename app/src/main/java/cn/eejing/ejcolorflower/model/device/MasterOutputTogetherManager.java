package cn.eejing.ejcolorflower.model.device;

/**
 * 齐喷管理
 */

public class MasterOutputTogetherManager extends MasterOutputManager {
    private int durationTime;   // 单位 0.1s
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
        long outputTime = this.durationTime;
        for (int i = 0; i < this.deviceCount; i++) {
            if (this.currentTime <= outputTime) {
                dataOut[i] = outHigh;
            } else {
                dataOut[i] = 0;
            }
        }

        return this.currentTime > outputTime;
    }

}
