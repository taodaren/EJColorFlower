package cn.eejing.ejcolorflower.model.request;

import java.util.List;

public class AddrCitysBean {

    /**
     * code : 1
     * message : 操作成功!
     * data : [{"id":50,"city_id":"220100","city":"长春市","province_id":"220000"},{"id":51,"city_id":"220200","city":"吉林市","province_id":"220000"},{"id":52,"city_id":"220300","city":"四平市","province_id":"220000"},{"id":53,"city_id":"220400","city":"辽源市","province_id":"220000"},{"id":54,"city_id":"220500","city":"通化市","province_id":"220000"},{"id":55,"city_id":"220600","city":"白山市","province_id":"220000"},{"id":56,"city_id":"220700","city":"松原市","province_id":"220000"},{"id":57,"city_id":"220800","city":"白城市","province_id":"220000"},{"id":58,"city_id":"222400","city":"延边朝鲜族自治州","province_id":"220000"}]
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
         * id : 50
         * city_id : 220100
         * city : 长春市
         * province_id : 220000
         */

        private int id;
        private String city_id;
        private String city;
        private String province_id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCity_id() {
            return city_id;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getProvince_id() {
            return province_id;
        }

        public void setProvince_id(String province_id) {
            this.province_id = province_id;
        }
    }
}
