package cn.eejing.ejcolorflower.model.lite;

import org.litepal.crud.LitePalSupport;

/**
 * 主控输出控制管理
 */

public class MasterCtrlModeEntity extends LitePalSupport {
    private String devId;        // 主控设备 ID
    private String type;         // 喷射效果
    private long millis;         // 时间戳

    // 主控喷射相关
    public int devCount;         // 设备数量
    public int loopId;           // 循环 ID
    public int currentTime;      // 当前时间

    public MasterCtrlModeEntity() {
    }

    public MasterCtrlModeEntity(String type, long millis) {
        this.type = type;
        this.millis = millis;
    }

    public MasterCtrlModeEntity(String type) {
        this.type = type;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public int getDevCount() {
        return devCount;
    }

    public void setDevCount(int devCount) {
        this.devCount = devCount;
    }

    public int getLoopId() {
        return loopId;
    }

    public void setLoopId(int loopId) {
        this.loopId = loopId;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
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
