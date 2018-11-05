package cn.eejing.colorflower.model.request;

import java.util.List;

public class GoodsDetailsBean {

    /**
     * code : 1
     * message : 操作成功!
     * data : {"name":"炫彩烟花机","money":0.01,"sold":"101","image":["http://60.205.226.109/uploads/goods/20180521/2ee6635624f23464e6f61ca86172da96.jpg","http://60.205.226.109/uploads/goods/20180521/19653ef1b5fd81886b105abee9d55187.jpg"],"stock":1,"postage":3299,"basics_postage":0,"url":"http://60.205.226.109/index/api/goods_details/id/1","phone":"123456789"}
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
         * name : 炫彩烟花机
         * money : 0.01
         * sold : 101
         * image : ["http://60.205.226.109/uploads/goods/20180521/2ee6635624f23464e6f61ca86172da96.jpg","http://60.205.226.109/uploads/goods/20180521/19653ef1b5fd81886b105abee9d55187.jpg"]
         * stock : 1
         * postage : 3299
         * basics_postage : 0
         * url : http://60.205.226.109/index/api/goods_details/id/1
         * phone : 123456789
         */

        private String name;
        private double money;
        private String sold;
        private int stock;
        private int postage;
        private int basics_postage;
        private String url;
        private String phone;
        private List<String> image;

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

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public int getPostage() {
            return postage;
        }

        public void setPostage(int postage) {
            this.postage = postage;
        }

        public int getBasics_postage() {
            return basics_postage;
        }

        public void setBasics_postage(int basics_postage) {
            this.basics_postage = basics_postage;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public List<String> getImage() {
            return image;
        }

        public void setImage(List<String> image) {
            this.image = image;
        }
    }
}
