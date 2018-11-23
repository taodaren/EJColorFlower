package cn.eejing.colorflower.model.request;

public class LoginBean {

    /**
     * code : 1
     * message : 登录成功
     * data : {"token":"GO0meZ1K4WS3gnfMprxE","level":"1","user_id":58}
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
         * token : GO0meZ1K4WS3gnfMprxE
         * level : 1
         * user_id : 58
         */

        private String token;
        private String level;
        private int user_id;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }
    }
}

