package cn.eejing.colorflower.model.lite;

import org.litepal.crud.LitePalSupport;

public class VipLvLite extends LitePalSupport {
    private String phone;
    private String userLevel;

    public VipLvLite() {
    }

    public VipLvLite(String phone, String userLevel) {
        this.phone = phone;
        this.userLevel = userLevel;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }
}
