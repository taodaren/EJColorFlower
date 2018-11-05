package cn.eejing.colorflower.model.request;

public class OrderDetailsBean {

    /**
     * code : 1
     * message : 获取成功
     * data : {"order_id":133,"order_num":"2018062307094822544kogg","goods_name":"炫彩烟花机","name":"android","mobile":"18511211125","quantity":2,"money":0.01,"image":"http://60.205.226.109/uploads/goods/20180521/2ee6635624f23464e6f61ca86172da96.jpg","address":"丁各庄村72号博光润泽有限公司","order_time":"2018-06-23 07:09:48"}
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
         * order_id : 133
         * order_num : 2018062307094822544kogg
         * goods_name : 炫彩烟花机
         * name : android
         * mobile : 18511211125
         * quantity : 2
         * money : 0.01
         * image : http://60.205.226.109/uploads/goods/20180521/2ee6635624f23464e6f61ca86172da96.jpg
         * address : 丁各庄村72号博光润泽有限公司
         * order_time : 2018-06-23 07:09:48
         */

        private int order_id;
        private String order_num;
        private String goods_name;
        private String name;
        private String mobile;
        private int quantity;
        private double money;
        private String image;
        private String address;
        private String order_time;

        public int getOrder_id() {
            return order_id;
        }

        public void setOrder_id(int order_id) {
            this.order_id = order_id;
        }

        public String getOrder_num() {
            return order_num;
        }

        public void setOrder_num(String order_num) {
            this.order_num = order_num;
        }

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
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

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getOrder_time() {
            return order_time;
        }

        public void setOrder_time(String order_time) {
            this.order_time = order_time;
        }
    }
}
