package cn.eejing.colorflower.model.request;

public class CallBackConfirmBean {

    /**
     * code : 0
     * message : 订单未支付
     */

    private int code;
    private String message;

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
}
