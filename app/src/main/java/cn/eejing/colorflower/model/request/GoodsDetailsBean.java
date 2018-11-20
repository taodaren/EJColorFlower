package cn.eejing.colorflower.model.request;

import java.util.List;

/**
 * 商品详情实体类
 */

public class GoodsDetailsBean {

    /**
     * code : 1
     * message : 数据获取成功
     * data : {"goods_sn":"YJV2Beta001","goods_name":"DMX屏蔽线","store_count":"10000000","price":"20.00","goods_remark":"DMX屏蔽线，用于彩花机设备之间的连接，保证设备多台分组同时控制。","original_img":["http://v2.eejing.cn/uploads/images/4396cbac91ad0110f955b12b28ffb4cc.jpg"],"sales_sum":"0","weight":"0","sale_price":"10.00","h5_detail":"http://v2.eejing.cn/api/v2.0/goodsDetailH5/12","server_tel":"15010986301"}
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
         * goods_sn : YJV2Beta001
         * goods_name : DMX屏蔽线
         * store_count : 10000000
         * price : 20.00
         * goods_remark : DMX屏蔽线，用于彩花机设备之间的连接，保证设备多台分组同时控制。
         * original_img : ["http://v2.eejing.cn/uploads/images/4396cbac91ad0110f955b12b28ffb4cc.jpg"]
         * sales_sum : 0
         * weight : 0
         * sale_price : 10.00
         * h5_detail : http://v2.eejing.cn/api/v2.0/goodsDetailH5/12
         * server_tel : 15010986301
         */

        private String goods_sn;
        private String goods_name;
        private String store_count;
        private String price;
        private String goods_remark;
        private String sales_sum;
        private String weight;
        private String sale_price;
        private String h5_detail;
        private String server_tel;
        private List<String> original_img;

        public String getGoods_sn() {
            return goods_sn;
        }

        public void setGoods_sn(String goods_sn) {
            this.goods_sn = goods_sn;
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

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getGoods_remark() {
            return goods_remark;
        }

        public void setGoods_remark(String goods_remark) {
            this.goods_remark = goods_remark;
        }

        public String getSales_sum() {
            return sales_sum;
        }

        public void setSales_sum(String sales_sum) {
            this.sales_sum = sales_sum;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getSale_price() {
            return sale_price;
        }

        public void setSale_price(String sale_price) {
            this.sale_price = sale_price;
        }

        public String getH5_detail() {
            return h5_detail;
        }

        public void setH5_detail(String h5_detail) {
            this.h5_detail = h5_detail;
        }

        public String getServer_tel() {
            return server_tel;
        }

        public void setServer_tel(String server_tel) {
            this.server_tel = server_tel;
        }

        public List<String> getOriginal_img() {
            return original_img;
        }

        public void setOriginal_img(List<String> original_img) {
            this.original_img = original_img;
        }
    }
}
