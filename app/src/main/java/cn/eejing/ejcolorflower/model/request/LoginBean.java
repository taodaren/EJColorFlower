package cn.eejing.ejcolorflower.model.request;

public class LoginBean {

    private int code;
    private String message;
    private LoginData data;

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

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }

    public class LoginData {
        private String token;
        private long member_id;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public long getMember_id() {
            return member_id;
        }

        public void setMember_id(long member_id) {
            this.member_id = member_id;
        }
    }

}

