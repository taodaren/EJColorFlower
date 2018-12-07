package cn.eejing.colorflower.model.request;

public class TokenBean {
    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public DataBean getData() {
        return data;
    }

    public static class DataBean {
        private String token;

        public String getToken() {
            return token;
        }
    }
}
