package cn.eejing.ejcolorflower.model.device;

/**
 * 主控输出管理
 */

public class MasterOutputManager {
    int deviceCount;    // 循环次数
    int loopCount;      // 当前循环的次数
    int loopId;
    int currentTime;

    public void configureStartData() {
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

}
