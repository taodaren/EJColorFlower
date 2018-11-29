package cn.eejing.colorflower.model.request;

public class OpenWalletBean {

    /**
     * code : 1
     * message : 数据获取成功
     * data : {"money":"23633244.00","pwd_status":1}
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
        /**
         * money : 23633244.00
         * pwd_status : 1
         */

        private String money;
        private int pwd_status;

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public int getPwd_status() {
            return pwd_status;
        }

        public void setPwd_status(int pwd_status) {
            this.pwd_status = pwd_status;
        }
    }
}
