package cn.eejing.colorflower.model.request;

public class OrderSetBean {

    /**
     * code : 1
     * message : 操作成功
     * data : {"order_sn":"YJ181121140801IhUIVx","total_amount":0.02}
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
         * order_sn : YJ181121140801IhUIVx
         * total_amount : 0.02
         */

        private String order_sn;
        private double total_amount;

        public String getOrder_sn() {
            return order_sn;
        }

        public void setOrder_sn(String order_sn) {
            this.order_sn = order_sn;
        }

        public double getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(double total_amount) {
            this.total_amount = total_amount;
        }
    }
}
