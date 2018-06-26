package cn.eejing.ejcolorflower.model.request;

import java.util.List;

public class AddrAreasBean {

    /**
     * code : 1
     * message : 操作成功!
     * data : [{"id":607,"area_id":"220502","area":"东昌区","city_id":"220500"},{"id":608,"area_id":"220503","area":"二道江区","city_id":"220500"},{"id":609,"area_id":"220521","area":"通化县","city_id":"220500"},{"id":610,"area_id":"220523","area":"辉南县","city_id":"220500"},{"id":611,"area_id":"220524","area":"柳河县","city_id":"220500"},{"id":612,"area_id":"220581","area":"梅河口市","city_id":"220500"},{"id":613,"area_id":"220582","area":"集安市","city_id":"220500"}]
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
         * id : 607
         * area_id : 220502
         * area : 东昌区
         * city_id : 220500
         */

        private int id;
        private String area_id;
        private String area;
        private String city_id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getArea_id() {
            return area_id;
        }

        public void setArea_id(String area_id) {
            this.area_id = area_id;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getCity_id() {
            return city_id;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }
    }
}
