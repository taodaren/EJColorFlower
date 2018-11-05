package cn.eejing.colorflower.model.request;

public class AboutLinkBean {

    /**
     * code : 1
     * message : 操作成功
     * data : http://60.205.226.109/admin/about/html
     */

    private int code;
    private String message;
    private String data;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
