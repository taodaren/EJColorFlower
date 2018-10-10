package cn.eejing.ejcolorflower.model.request;

public class QueryDevMacBean {

    /**
     * code : 1
     * message : 数据获取成功
     * data : {"id":"810361","mac":"FF:0D:FB:3A:FB:84"}
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
         * id : 810361
         * mac : FF:0D:FB:3A:FB:84
         */

        private String id;
        private String mac;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }
    }
}
