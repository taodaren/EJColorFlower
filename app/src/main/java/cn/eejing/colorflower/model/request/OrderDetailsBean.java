package cn.eejing.colorflower.model.request;

public class OrderDetailsBean {

    /**
     * code : 1
     * message : 获取数据成功
     * data : {"consignee":"脱离","mobile":"18512312312","address":"内蒙古自治区 通辽市 科左后旗 木","order_sn":"YJ181121180025JNE32w","create_time":"2018-11-21 18:00:25","goods_name":"滑雪板(测试)","total_price":"0.04","original_img":"http://v2.eejing.cn/uploads/images/WechatIMG37.jpeg","quantity":"2"}
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
         * consignee : 脱离
         * mobile : 18512312312
         * address : 内蒙古自治区 通辽市 科左后旗 木
         * order_sn : YJ181121180025JNE32w
         * create_time : 2018-11-21 18:00:25
         * goods_name : 滑雪板(测试)
         * total_price : 0.04
         * original_img : http://v2.eejing.cn/uploads/images/WechatIMG37.jpeg
         * quantity : 2
         */

        private String consignee;
        private String mobile;
        private String address;
        private String order_sn;
        private String create_time;
        private String goods_name;
        private String total_price;
        private String original_img;
        private String quantity;

        public String getConsignee() {
            return consignee;
        }

        public void setConsignee(String consignee) {
            this.consignee = consignee;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getOrder_sn() {
            return order_sn;
        }

        public void setOrder_sn(String order_sn) {
            this.order_sn = order_sn;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public String getTotal_price() {
            return total_price;
        }

        public void setTotal_price(String total_price) {
            this.total_price = total_price;
        }

        public String getOriginal_img() {
            return original_img;
        }

        public void setOriginal_img(String original_img) {
            this.original_img = original_img;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }
    }
}
