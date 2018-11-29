package cn.eejing.colorflower.model.request;

import java.util.List;

public class BillBean {

    /**
     * code : 1
     * message : 数据获取成功
     * data : [{"create_time":"2018-11-09 11:10:51","money":"-50","title":"提现","status":"0","type":"1","vip_mobile":""},{"create_time":"2018-11-07 11:43:59","money":"-13","title":"提现","status":"1","type":"1","vip_mobile":""},{"create_time":"2018-11-07 11:34:28","money":"+25.00","title":"申请驳回退款","status":"1","type":"2","vip_mobile":""},{"create_time":"2018-11-07 11:21:55","money":"-25","title":"提现","status":"2","type":"1","vip_mobile":""},{"create_time":"2018-11-07 10:37:10","money":"-49","title":"提现","status":"2","type":"1","vip_mobile":""}]
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
         * create_time : 2018-11-09 11:10:51
         * money : -50
         * title : 提现
         * status : 0
         * type : 1
         * vip_mobile :
         */

        private String create_time;
        private String money;
        private String title;
        private String status;  // 0-待处理 1-正常处理 2-驳回
        private String type;    // 0-返点 1-提现 2-驳回退还 3-vip购物返点
        private String vip_mobile;

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVip_mobile() {
            return vip_mobile;
        }

        public void setVip_mobile(String vip_mobile) {
            this.vip_mobile = vip_mobile;
        }
    }
}
