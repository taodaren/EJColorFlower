package cn.eejing.colorflower.model.request;

import java.util.List;

public class OrderListBean {

    /**
     * code : 1
     * message : 数据获取成功
     * data : [{"order_sn":"YJ181121175142qhwOXr","goods_name":"滑雪板(测试)","quantity":"1","goods_price":"0.02","total_amount":"0.02","goods_img":"http://v2.eejing.cn/uploads/images/WechatIMG37.jpeg"}]
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
         * order_sn : YJ181121175142qhwOXr
         * goods_name : 滑雪板(测试)
         * quantity : 1
         * goods_price : 0.02
         * total_amount : 0.02
         * goods_img : http://v2.eejing.cn/uploads/images/WechatIMG37.jpeg
         */

        private String order_sn;
        private String goods_name;
        private String quantity;
        private String goods_price;
        private String total_amount;
        private String goods_img;

        public String getOrder_sn() {
            return order_sn;
        }

        public void setOrder_sn(String order_sn) {
            this.order_sn = order_sn;
        }

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getGoods_price() {
            return goods_price;
        }

        public void setGoods_price(String goods_price) {
            this.goods_price = goods_price;
        }

        public String getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(String total_amount) {
            this.total_amount = total_amount;
        }

        public String getGoods_img() {
            return goods_img;
        }

        public void setGoods_img(String goods_img) {
            this.goods_img = goods_img;
        }
    }
}