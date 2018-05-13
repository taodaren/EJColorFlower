package cn.eejing.ejcolorflower.model.request;

import java.util.List;

/**
 * @创建者 Taodaren
 * @描述
 */
public class GoodsListBean {
    private int code;
    private String message;
    private List<Data> data;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
        return data;
    }

    public static class Data {

        private int group_id;
        private List<String> group_list;
        private int update_time;
        private String group_name;
        private double high;

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }

        public int getGroup_id() {
            return group_id;
        }

        public void setGroup_list(List<String> group_list) {
            this.group_list = group_list;
        }

        public List<String> getGroup_list() {
            return group_list;
        }

        public void setUpdate_time(int update_time) {
            this.update_time = update_time;
        }

        public int getUpdate_time() {
            return update_time;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setHigh(double high) {
            this.high = high;
        }

        public double getHigh() {
            return high;
        }

    }

}
