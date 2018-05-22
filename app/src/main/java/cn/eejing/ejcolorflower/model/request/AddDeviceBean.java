package cn.eejing.ejcolorflower.model.request;

/**
 * @创建者 Taodaren
 * @描述
 */
public class AddDeviceBean {

    /**
     * code : 5
     * message : 该设备不存在
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
