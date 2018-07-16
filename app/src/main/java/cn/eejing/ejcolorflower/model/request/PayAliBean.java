package cn.eejing.ejcolorflower.model.request;

public class PayAliBean {

    /**
     * code : 1
     * message : orderString码
     * data : {"orderString":"alipay_sdk=alipay-sdk-php-20161101&app_id=2018052360223153&biz_content=%7B%22body%22%3A%22%E8%AE%A2%E5%8D%95%E6%94%AF%E4%BB%98%22%2C%22subject%22%3A+%22%E7%82%AB%E5%BD%A9%E7%83%9F%E8%8A%B1%E6%9C%BA%22%2C%22out_trade_no%22%3A+%222018062116181145770sbhy%22%2C%22timeout_express%22%3A+%2230m%22%2C%22total_amount%22%3A+%220.01%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F60.205.226.109%2Findex.php%2Findex%2Fapi%2FpayCallbBack&sign_type=RSA2&timestamp=2018-06-21+16%3A18%3A11&version=1.0&sign=NUTo8Lg%2BGBm7Mk6TEJ6qHLplz%2FCr3Iid9blB7zL%2FItMpbe7BAWCRHVbgiL%2BKMkj5g2QaEiH%2BSiA8066VHkg%2F5fLJlD6%2B1TIvVUF7%2F4ux1dWJDtvym9RNsjrokF%2BM7xfwpC1cvQfA89vg2s%2FKcn9NR0pia%2Bthl6IpeUwoXSjy9SZZVnxGGMP9vZKj0U2YOiYxfJ9OGI33JML4dIWsdaIW0c27RuAv0i7NbU5BNR9fNg3S9oz1YlBCFS5ZBmf8iS92mgeMzej8iRS0o%2FZsGMKxxSBoiQM3c8UTaLSzQyPxDqB%2B9V1MHmes1IA0chUY8DGcLQjWhc6uSAKZZQPIKML6WQ%3D%3D"}
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
         * orderString : alipay_sdk=alipay-sdk-php-20161101&app_id=2018052360223153&biz_content=%7B%22body%22%3A%22%E8%AE%A2%E5%8D%95%E6%94%AF%E4%BB%98%22%2C%22subject%22%3A+%22%E7%82%AB%E5%BD%A9%E7%83%9F%E8%8A%B1%E6%9C%BA%22%2C%22out_trade_no%22%3A+%222018062116181145770sbhy%22%2C%22timeout_express%22%3A+%2230m%22%2C%22total_amount%22%3A+%220.01%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F60.205.226.109%2Findex.php%2Findex%2Fapi%2FpayCallbBack&sign_type=RSA2&timestamp=2018-06-21+16%3A18%3A11&version=1.0&sign=NUTo8Lg%2BGBm7Mk6TEJ6qHLplz%2FCr3Iid9blB7zL%2FItMpbe7BAWCRHVbgiL%2BKMkj5g2QaEiH%2BSiA8066VHkg%2F5fLJlD6%2B1TIvVUF7%2F4ux1dWJDtvym9RNsjrokF%2BM7xfwpC1cvQfA89vg2s%2FKcn9NR0pia%2Bthl6IpeUwoXSjy9SZZVnxGGMP9vZKj0U2YOiYxfJ9OGI33JML4dIWsdaIW0c27RuAv0i7NbU5BNR9fNg3S9oz1YlBCFS5ZBmf8iS92mgeMzej8iRS0o%2FZsGMKxxSBoiQM3c8UTaLSzQyPxDqB%2B9V1MHmes1IA0chUY8DGcLQjWhc6uSAKZZQPIKML6WQ%3D%3D
         */

        private String orderString;

        public String getOrderString() {
            return orderString;
        }

        public void setOrderString(String orderString) {
            this.orderString = orderString;
        }
    }
}
