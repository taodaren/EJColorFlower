package cn.eejing.colorflower.model.request;

import java.util.List;

public class EditDeviceToGroupBean {

    /**
     * code : 1
     * message : 设备获取成功
     * data : {"possess":["810303"],"list":["810316"]}
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
        private List<String> possess;
        private List<String> list;

        public List<String> getPossess() {
            return possess;
        }

        public void setPossess(List<String> possess) {
            this.possess = possess;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }

}
