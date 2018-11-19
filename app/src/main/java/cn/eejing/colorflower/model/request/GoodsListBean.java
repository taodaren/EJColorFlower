package cn.eejing.colorflower.model.request;

import java.util.List;

/**
 * 商品列表实体类
 */

public class GoodsListBean {

    /**
     * code : 1
     * message : 数据获取成功
     * data : [{"id":12,"goods_name":"DMX屏蔽线","store_count":"10000000","price":"20.00","goods_remark":"DMX屏蔽线，用于彩花机设备之间的连接，保证设备多台分组同时控制。","original_img":"http://v2.eejing.cn/uploads/images/4396cbac91ad0110f955b12b28ffb4cc.jpg","sales_sum":"0","sale_price":20},{"id":13,"goods_name":"彩花机料包(小)","store_count":"100000000","price":"35.00","goods_remark":"烟花燃料,采用无污染材料, 燃烧时间长,样式好看, 分大小包装, 多种颜色任你选择","original_img":"http://v2.eejing.cn/uploads/images/39f254e75cb986e0bc65720a3807f41d.jpg","sales_sum":"0","sale_price":35},{"id":14,"goods_name":"彩花机料包(大)","store_count":"10000000","price":"128.00","goods_remark":"烟花燃料,采用无污染材料, 燃烧时间长,样式好看, 分大小包装, 多种颜色任你选择","original_img":"http://v2.eejing.cn/uploads/images/6a067ddee642303ffc8ea72ab6c7a772.jpg","sales_sum":"0","sale_price":128},{"id":15,"goods_name":"异景彩花机(全能版)","store_count":"10000000","price":"2880.00","goods_remark":"本公司自行研制开发制作的机器, 可手机遥控","original_img":"http://v2.eejing.cn/uploads/images/22e81fffb959d5539ea8f370f8240028.jpg","sales_sum":"0","sale_price":2880},{"id":11,"goods_name":"滑雪板(测试)","store_count":"0","price":"0.10","goods_remark":"再一次来到深圳，再次来到广东，我们就是要在这里向世界宣示","original_img":"http://v2.eejing.cn/uploads/images/WechatIMG37.jpeg","sales_sum":"0","sale_price":0.1}]
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
         * id : 12
         * goods_name : DMX屏蔽线
         * store_count : 10000000
         * price : 20.00
         * goods_remark : DMX屏蔽线，用于彩花机设备之间的连接，保证设备多台分组同时控制。
         * original_img : http://v2.eejing.cn/uploads/images/4396cbac91ad0110f955b12b28ffb4cc.jpg
         * sales_sum : 0
         * sale_price : 20
         */

        private int id;
        private String goods_name;
        private String store_count;
        private String price;
        private String goods_remark;
        private String original_img;
        private String sales_sum;
        private float sale_price;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getOriginal_img() {
            return original_img;
        }

        public void setOriginal_img(String original_img) {
            this.original_img = original_img;
        }

        public String getSales_sum() {
            return sales_sum;
        }

        public void setSales_sum(String sales_sum) {
            this.sales_sum = sales_sum;
        }

        public float getSale_price() {
            return sale_price;
        }

        public void setSale_price(float sale_price) {
            this.sale_price = sale_price;
        }
    }
}
