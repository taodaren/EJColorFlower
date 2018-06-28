package cn.eejing.ejcolorflower.model.event;

/**
 * 喷射效果控制
 */

public class JetStatusEvent {
    private String mType;      // 效果功能（方式）
    private String mDirection; // 方向
    private String mGap;       // 间隔时间
    private String mDuration;  // 持续时间
    private String mGapBig;    // 大间隔时间
    private String mLoop;      // 循环次数
    private String mFrequency; // 次数（换向）
    private String mHigh;      // 高度
    private int mGroupId;

    public JetStatusEvent() {
    }

    public JetStatusEvent(String mType, String mDirection, String mGap, String mDuration, String mGapBig, String mLoop, int mGroupId, String mHigh) {
        this.mType = mType;
        this.mDirection = mDirection;
        this.mGap = mGap;
        this.mDuration = mDuration;
        this.mGapBig = mGapBig;
        this.mLoop = mLoop;
        this.mGroupId = mGroupId;
        this.mHigh = mHigh;
    }

    public JetStatusEvent(String mType, String mGap, String mDuration, String mFrequency, int mGroupId, String mHigh) {
        this.mType = mType;
        this.mGap = mGap;
        this.mDuration = mDuration;
        this.mFrequency = mFrequency;
        this.mGroupId = mGroupId;
        this.mHigh = mHigh;
    }

    public JetStatusEvent(String mType, String mDuration, String mHigh, int mGroupId) {
        this.mType = mType;
        this.mDuration = mDuration;
        this.mHigh = mHigh;
        this.mGroupId = mGroupId;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getmDirection() {
        return mDirection;
    }

    public void setDirection(String mDirection) {
        this.mDirection = mDirection;
    }

    public String getGap() {
        return mGap;
    }

    public void setGap(String mGap) {
        this.mGap = mGap;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public String getGapBig() {
        return mGapBig;
    }

    public void setGapBig(String mGapBig) {
        this.mGapBig = mGapBig;
    }

    public String getLoop() {
        return mLoop;
    }

    public void setLoop(String mLoop) {
        this.mLoop = mLoop;
    }

    public String getFrequency() {
        return mFrequency;
    }

    public void setFrequency(String mFrequency) {
        this.mFrequency = mFrequency;
    }

    public String getHigh() {
        return mHigh;
    }

    public void setHigh(String mHigh) {
        this.mHigh = mHigh;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public void setGroupId(int mGroupId) {
        this.mGroupId = mGroupId;
    }
}
