package cn.eejing.colorflower.model.request;

import java.io.Serializable;
import java.util.List;

public class AddrListBean {

    /**
     * code : 1
     * message : 数据获取成功
     * data : [{"id":53,"consignee":"李","province":"北京市","city":"北京市","district":"东城区","address":"东邪西毒","mobile":"17777777777","is_default":"1","created_at":"2018-11-01 17:49:42","updated_at":"2018-11-01 17:49:42","province_id":"1","city_id":"2","district_id":"3"},{"id":54,"consignee":"据了解","province":"北京市","city":"县","district":"延庆县","address":"i 亏咯直接","mobile":"12475285155","is_default":"0","created_at":"2018-11-01 17:49:42","updated_at":"2018-11-01 17:49:42","province_id":"1","city_id":"300","district_id":"322"}]
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

    public static class DataBean implements Serializable {
        /**
         * id : 53
         * consignee : 李
         * province : 北京市
         * city : 北京市
         * district : 东城区
         * address : 东邪西毒
         * mobile : 17777777777
         * is_default : 1
         * created_at : 2018-11-01 17:49:42
         * updated_at : 2018-11-01 17:49:42
         * province_id : 1
         * city_id : 2
         * district_id : 3
         */

        private int id;
        private String consignee;
        private String province;
        private String city;
        private String district;
        private String address;
        private String mobile;
        private String is_default;
        private String created_at;
        private String updated_at;
        private String province_id;
        private String city_id;
        private String district_id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getConsignee() {
            return consignee;
        }

        public void setConsignee(String consignee) {
            this.consignee = consignee;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getIs_default() {
            return is_default;
        }

        public void setIs_default(String is_default) {
            this.is_default = is_default;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getProvince_id() {
            return province_id;
        }

        public void setProvince_id(String province_id) {
            this.province_id = province_id;
        }

        public String getCity_id() {
            return city_id;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }

        public String getDistrict_id() {
            return district_id;
        }

        public void setDistrict_id(String district_id) {
            this.district_id = district_id;
        }
    }
}
