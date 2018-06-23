package cn.eejing.ejcolorflower.model.request;

public class PwdUpdateBean {

    /**
     * code : 3
     * message : 旧密码不能为空
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
