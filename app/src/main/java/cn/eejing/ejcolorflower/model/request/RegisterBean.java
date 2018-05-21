package cn.eejing.ejcolorflower.model.request;

/**
 * @创建者 Taodaren
 * @描述
 */
public class RegisterBean {

    /**
     * code : 6
     * message : 验证码不正确
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
