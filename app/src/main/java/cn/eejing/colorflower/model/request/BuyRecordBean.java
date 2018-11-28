package cn.eejing.colorflower.model.request;

import java.util.List;

/**
 * 购买记录实体类
 */

public class BuyRecordBean {

    /**
     * code : 1
     * message : 数据获取成功
     * data : [{"goods_sn":"GSVOAM2514","goods_name":"滑雪板(测试)","quantity":"1","create_time":"2018-11-05 15:46:23"},{"goods_sn":"GSVOAM2514","goods_name":"滑雪板(测试)","quantity":"1","create_time":"2018-11-01 15:22:24"},{"goods_sn":"GSVOAM2514","goods_name":"滑雪板(测试)","quantity":"1","create_time":"2018-11-01 17:33:10"},{"goods_sn":"GSVOAM2514","goods_name":"滑雪板(测试)","quantity":"1","create_time":"2018-11-13 15:08:17"},{"goods_sn":"GSVOAM2514","goods_name":"滑雪板(测试)","quantity":"1","create_time":"2018-11-01 17:27:57"},{"goods_sn":"GSVOAM2514","goods_name":"滑雪板(测试)","quantity":"1","create_time":"2018-11-01 15:22:11"},{"goods_sn":"GSVOAM2514","goods_name":"滑雪板(测试)","quantity":"1","create_time":"2018-11-01 16:17:05"},{"goods_sn":"GSVOAM2514","goods_name":"滑雪板(测试)","quantity":"1","create_time":"2018-11-01 17:27:00"},{"goods_sn":"GSVOAM2514","goods_name":"滑雪板(测试)","quantity":"1","create_time":"2018-11-01 16:43:03"}]
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
         * goods_sn : GSVOAM2514
         * goods_name : 滑雪板(测试)
         * quantity : 1
         * create_time : 2018-11-05 15:46:23
         */

        private String goods_sn;
        private String goods_name;
        private String quantity;
        private String create_time;

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

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }
    }
}
