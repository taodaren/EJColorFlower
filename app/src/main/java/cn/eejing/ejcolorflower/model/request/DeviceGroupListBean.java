package cn.eejing.ejcolorflower.model.request;

import java.util.List;


public class DeviceGroupListBean {

    /**
     * code : 1
     * message : 获取数据成功
     * data : [{"group_id":87,"group_list":["810316"],"update_time":1525225792,"group_name":"组1","high":4.7}]
     */

    private int code;
    private String message;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * group_id : 87
         * group_list : ["810316"]
         * update_time : 1525225792
         * group_name : 组1
         * high : 4.7
         */

        private int group_id;
        private int update_time;
        private String group_name;
        private double high;
        private List<String> group_list;

        public int getGroup_id() {
            return group_id;
        }

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }

        public int getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(int update_time) {
            this.update_time = update_time;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public double getHigh() {
            return high;
        }

        public void setHigh(double high) {
            this.high = high;
        }

        public List<String> getGroup_list() {
            return group_list;
        }

        public void setGroup_list(List<String> group_list) {
            this.group_list = group_list;
        }
    }
}