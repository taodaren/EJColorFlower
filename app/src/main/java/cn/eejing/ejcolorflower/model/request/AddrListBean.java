package cn.eejing.ejcolorflower.model.request;

import java.io.Serializable;
import java.util.List;

public class AddrListBean {

    /**
     * code : 1
     * message : 操作成功!
     * data : [{"id":1,"member_id":15,"name":"胡斐","mobile":"18666666666","province_id":620000,"city_id":622900,"area_id":622927,"address":"天山","address_all":"甘肃省临夏回族自治州积石山保安族东乡族撒拉族自治县天山","status":1,"add_time":"1525829655","addres_two":"甘肃省 临夏回族自治州 积石山保安族东乡族撒拉族自治县"},{"id":25,"member_id":15,"name":"令狐冲","mobile":"15899999945","province_id":230000,"city_id":231100,"area_id":231121,"address":"北极","address_all":"黑龙江黑河市嫩江县北极","status":0,"add_time":"1529898826","addres_two":"黑龙江 黑河市 嫩江县"}]
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
         * id : 1
         * member_id : 15
         * name : 胡斐
         * mobile : 18666666666
         * province_id : 620000
         * city_id : 622900
         * area_id : 622927
         * address : 天山
         * address_all : 甘肃省临夏回族自治州积石山保安族东乡族撒拉族自治县天山
         * status : 1
         * add_time : 1525829655
         * addres_two : 甘肃省 临夏回族自治州 积石山保安族东乡族撒拉族自治县
         */

        private int id;
        private int member_id;
        private String name;
        private String mobile;
        private int province_id;
        private int city_id;
        private int area_id;
        private String address;
        private String address_all;
        private int status;
        private String add_time;
        private String addres_two;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getMember_id() {
            return member_id;
        }

        public void setMember_id(int member_id) {
            this.member_id = member_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public int getProvince_id() {
            return province_id;
        }

        public void setProvince_id(int province_id) {
            this.province_id = province_id;
        }

        public int getCity_id() {
            return city_id;
        }

        public void setCity_id(int city_id) {
            this.city_id = city_id;
        }

        public int getArea_id() {
            return area_id;
        }

        public void setArea_id(int area_id) {
            this.area_id = area_id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress_all() {
            return address_all;
        }

        public void setAddress_all(String address_all) {
            this.address_all = address_all;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getAddres_two() {
            return addres_two;
        }

        public void setAddres_two(String addres_two) {
            this.addres_two = addres_two;
        }
    }

}
