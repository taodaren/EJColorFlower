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
     * data : [{"goods_id":1,"name":"料包","money":50,"sold":"500","image":"http://60.205.226.109/uploads/goods/20180511/9f6556b744db2c89368838abf7588564.jpg","stock":1},{"goods_id":2,"name":"炫彩无敌小火花","money":500,"sold":"3000","image":"http://60.205.226.109/uploads/goods/20180511/4c224386671fa97601258ebb855e127d.jpg","stock":1},{"goods_id":3,"name":"花料","money":5000,"sold":"50000","image":"http://60.205.226.109/uploads/goods/20180511/7b1c1cb0b5573ea1b14354b232eae3bc.jpg","stock":0},{"goods_id":4,"name":"无敌小火花","money":50,"sold":"400","image":"http://60.205.226.109/uploads/goods/20180511/4528d302c61f5a05b78fda02ff3bc444.jpg","stock":1},{"goods_id":5,"name":"小旋风彩彩花","money":789,"sold":"987","image":"http://60.205.226.109/uploads/goods/20180511/58ccf875a1f9216bccc44fe728a9e515.jpg","stock":1},{"goods_id":7,"name":"小旋风彩彩花","money":520,"sold":"3000","image":"http://60.205.226.109/uploads/goods\\20180511\\67c23dff5ba9f38208e7fdc72b9d7306.jpg","stock":1}]
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
         * name : 料包
         * money : 50
         * sold : 500
         * image : http://60.205.226.109/uploads/goods/20180511/9f6556b744db2c89368838abf7588564.jpg
         * stock : 1
         */

        private int goods_id;
        private String name;
        private int money;
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

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
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
