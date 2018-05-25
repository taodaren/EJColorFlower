package cn.eejing.ejcolorflower.model.request;

import java.util.List;

/**
 * @创建者 Taodaren
 * @描述
 */
public class GoodsListBean {


    /**
     * code : 1
     * message : 操作成功
     * data : [{"goods_id":1,"name":"炫彩烟花机","money":0.01,"sold":"0","image":"http://60.205.226.109/uploads/goods/20180521/2ee6635624f23464e6f61ca86172da96.jpg","stock":1},{"goods_id":2,"name":"炫彩烟花料(大)","money":65,"sold":"0","image":"http://60.205.226.109/uploads/goods/20180521/4341a22a4401a936794e71ea53be7cc4.jpg","stock":1},{"goods_id":3,"name":"炫彩烟花料(中)","money":35,"sold":"0","image":"http://60.205.226.109/uploads/goods/20180521/d98826a9cbde167b070a041c56ec6a02.jpg","stock":1},{"goods_id":4,"name":"炫彩烟花机","money":555,"sold":"0","image":"http://60.205.226.109/uploads/goods/20180521/0a7422d413c023381c01b2e9fb87d540.jpg","stock":1}]
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
         * goods_id : 1
         * name : 炫彩烟花机
         * money : 0.01
         * sold : 0
         * image : http://60.205.226.109/uploads/goods/20180521/2ee6635624f23464e6f61ca86172da96.jpg
         * stock : 1
         */

        private int goods_id;
        private String name;
        private double money;
        private String sold;
        private String image;
        private int stock;

        public int getGoods_id() {
            return goods_id;
        }

        public void setGoods_id(int goods_id) {
            this.goods_id = goods_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }

        public String getSold() {
            return sold;
        }

        public void setSold(String sold) {
            this.sold = sold;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }
    }
}
