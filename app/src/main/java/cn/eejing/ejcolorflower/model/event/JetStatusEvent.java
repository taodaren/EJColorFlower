package cn.eejing.ejcolorflower.model.event;

/**
 * 喷射效果控制
 */

public class JetStatusEvent {
    private String mType;      // 效果功能（方式）
    private int mDirection; // 方向
    private int mGap;       // 间隔时间
    private int mDuration;  // 持续时间
    private int mGapBig;    // 大间隔时间
    private int mLoop;      // 循环次数
    private int mFrequency; // 次数（换向）
    private int mHigh;      // 高度
    private int mGroupId;

    public JetStatusEvent() {
    }

    public JetStatusEvent(String mType, int mDirection, int mGap, int mDuration, int mGapBig, int mLoop, int mGroupId, int mHigh) {
        this.mType = mType;
        this.mDirection = mDirection;
        this.mGap = mGap;
        this.mDuration = mDuration;
        this.mGapBig = mGapBig;
        this.mLoop = mLoop;
        this.mGroupId = mGroupId;
        this.mHigh = mHigh;
    }

    public JetStatusEvent(String mType, int mGap, int mDuration, int mFrequency, int mGroupId, int mHigh) {
        this.mType = mType;
        this.mGap = mGap;
        this.mDuration = mDuration;
        this.mFrequency = mFrequency;
        this.mGroupId = mGroupId;
        this.mHigh = mHigh;
    }

    public JetStatusEvent(String mType, int mDuration, int mHigh, int mGroupId) {
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

    public int getmDirection() {
        return mDirection;
    }

    public void setDirection(int mDirection) {
        this.mDirection = mDirection;
    }

    public int getGap() {
        return mGap;
    }

    public void setGap(int mGap) {
        this.mGap = mGap;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public int getGapBig() {
        return mGapBig;
    }

    public void setGapBig(int mGapBig) {
        this.mGapBig = mGapBig;
    }

    public int getLoop() {
        return mLoop;
    }

    public void setLoop(int mLoop) {
        this.mLoop = mLoop;
    }

    public int getFrequency() {
        return mFrequency;
    }

    public void setFrequency(int mFrequency) {
        this.mFrequency = mFrequency;
    }

    public int getHigh() {
        return mHigh;
    }

    public void setHigh(int mHigh) {
        this.mHigh = mHigh;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public void setGroupId(int mGroupId) {
        this.mGroupId = mGroupId;
    }
}
