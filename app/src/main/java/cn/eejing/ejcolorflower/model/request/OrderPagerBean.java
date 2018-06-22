package cn.eejing.ejcolorflower.model.request;

import java.util.List;

public class OrderPagerBean {

    /**
     * code : 1
     * message : 操作成功!
     * data : [{"order_id":124,"image":"http://60.205.226.109/uploads/goods/20180521/2ee6635624f23464e6f61ca86172da96.jpg","name":"炫彩烟花机","quantity":1,"postage":0,"money":0.01,"total_price":"0.01","status":"待发货","order_time":"1529577866"},{"order_id":85,"image":"http://60.205.226.109/uploads/goods/20180521/2ee6635624f23464e6f61ca86172da96.jpg","name":"炫彩烟花机","quantity":1,"postage":0,"money":0.01,"total_price":"0.01","status":"待发货","order_time":"1529549516"}]
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
         * order_id : 124
         * image : http://60.205.226.109/uploads/goods/20180521/2ee6635624f23464e6f61ca86172da96.jpg
         * name : 炫彩烟花机
         * quantity : 1
         * postage : 0
         * money : 0.01
         * total_price : 0.01
         * status : 待发货
         * order_time : 1529577866
         */

        private int order_id;
        private String image;
        private String name;
        private int quantity;
        private int postage;
        private double money;
        private String total_price;
        private String status;
        private String order_time;

        public int getOrder_id() {
            return order_id;
        }

        public void setOrder_id(int order_id) {
            this.order_id = order_id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getPostage() {
            return postage;
        }

        public void setPostage(int postage) {
            this.postage = postage;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }

        public String getTotal_price() {
            return total_price;
        }

        public void setTotal_price(String total_price) {
            this.total_price = total_price;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOrder_time() {
            return order_time;
        }

        public void setOrder_time(String order_time) {
            this.order_time = order_time;
        }
    }

}