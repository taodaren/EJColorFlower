package cn.eejing.colorflower.model.event;

public class JumpLoginEvent {
    private String jumpInfo;

    public JumpLoginEvent(String jumpInfo) {
        this.jumpInfo = jumpInfo;
    }

    public String getJumpInfo() {
        return jumpInfo;
    }

    public void setJumpInfo(String jumpInfo) {
        this.jumpInfo = jumpInfo;
    }
}
