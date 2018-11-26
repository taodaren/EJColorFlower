package cn.eejing.colorflower.model.request;

import java.util.List;

public class VipListBean {

    /**
     * code : 1
     * message : 数据获取成功
     * data : [{"id":51,"mobile":"18322785505","discount":"0.80","remark":"fsdfa"},{"id":56,"mobile":"15120099106","discount":"0.80","remark":"个快乐啊考虑图兔"},{"id":58,"mobile":"18511211125","discount":"1.00","remark":""},{"id":60,"mobile":"18514785450","discount":"1.00","remark":""}]
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
         * id : 51
         * mobile : 18322785505
         * discount : 0.80
         * remark : fsdfa
         */

        private int id;
        private String mobile;
        private String discount;
        private String remark;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
