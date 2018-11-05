package cn.eejing.colorflower.model.request;

public class AddMaterialBean {

    /**
     * code : 1
     * message : 料包已绑定,并已进入锁定状态
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
