package cn.eejing.colorflower.model.request;

import java.util.List;

public class UpgradeVipBean {

    /**
     * code : 1
     * message : 数据获取成功
     * data : {"condition":["1、有推荐人:输入推荐人手机号码即升级为VIP","2、没有推荐人:在线购买本公司产品满2800元即可升级为VIP"],"interests":["1、通过推荐人升级VIP,本公司产品价格根据推荐人设定折扣价格购买","2、无推荐人升级VIP,可根据公司给定的折扣购买产品"]}
     */

    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<String> condition;
        private List<String> interests;

        public List<String> getCondition() {
            return condition;
        }

        public void setCondition(List<String> condition) {
            this.condition = condition;
        }

        public List<String> getInterests() {
            return interests;
        }

        public void setInterests(List<String> interests) {
            this.interests = interests;
        }
    }
}
