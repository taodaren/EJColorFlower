package cn.eejing.colorflower.model.request;

/**
 * 确认订单实体类
 */

public class ConfirmOrderBean {

    /**
     * code : 1
     * message : 操作成功
     * data : {"goods":{"goods_remark":"DMX屏蔽线，用于彩花机设备之间的连接，保证设备多台分组同时控制。","price":"20.00","original_img":"http://v2.eejing.cn/uploads/images/4396cbac91ad0110f955b12b28ffb4cc.jpg","goods_name":"DMX屏蔽线","store_count":"10000000","id":12,"sale_price":"10.00"},"address":{"province":"北京市","city":"北京市","district":"东城区","address":"东邪西毒","mobile":"17777777777","consignee":"李","id":53}}
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
         * goods : {"goods_remark":"DMX屏蔽线，用于彩花机设备之间的连接，保证设备多台分组同时控制。","price":"20.00","original_img":"http://v2.eejing.cn/uploads/images/4396cbac91ad0110f955b12b28ffb4cc.jpg","goods_name":"DMX屏蔽线","store_count":"10000000","id":12,"sale_price":"10.00"}
         * address : {"province":"北京市","city":"北京市","district":"东城区","address":"东邪西毒","mobile":"17777777777","consignee":"李","id":53}
         */

        private GoodsBean goods;
        private AddressBean address;

        public GoodsBean getGoods() {
            return goods;
        }

        public void setGoods(GoodsBean goods) {
            this.goods = goods;
        }

        public AddressBean getAddress() {
            return address;
        }

        public void setAddress(AddressBean address) {
            this.address = address;
        }

        public static class GoodsBean {
            /**
             * goods_remark : DMX屏蔽线，用于彩花机设备之间的连接，保证设备多台分组同时控制。
             * price : 20.00
             * original_img : http://v2.eejing.cn/uploads/images/4396cbac91ad0110f955b12b28ffb4cc.jpg
             * goods_name : DMX屏蔽线
             * store_count : 10000000
             * id : 12
             * sale_price : 10.00
             */

            private String goods_remark;
            private String price;
            private String original_img;
            private String goods_name;
            private String store_count;
            private int id;
            private String sale_price;

            public String getGoods_remark() {
                return goods_remark;
            }

            public void setGoods_remark(String goods_remark) {
                this.goods_remark = goods_remark;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getOriginal_img() {
                return original_img;
            }

            public void setOriginal_img(String original_img) {
                this.original_img = original_img;
            }

            public String getGoods_name() {
                return goods_name;
            }

            public void setGoods_name(String goods_name) {
                this.goods_name = goods_name;
            }

            public String getStore_count() {
                return store_count;
            }

            public void setStore_count(String store_count) {
                this.store_count = store_count;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getSale_price() {
                return sale_price;
            }

            public void setSale_price(String sale_price) {
                this.sale_price = sale_price;
            }
        }

        public static class AddressBean {
            /**
             * province : 北京市
             * city : 北京市
             * district : 东城区
             * address : 东邪西毒
             * mobile : 17777777777
             * consignee : 李
             * id : 53
             */

            private String province;
            private String city;
            private String district;
            private String address;
            private String mobile;
            private String consignee;
            private int id;

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

            public String getConsignee() {
                return consignee;
            }

            public void setConsignee(String consignee) {
                this.consignee = consignee;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }
    }
}
