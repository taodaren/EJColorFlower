package cn.eejing.colorflower.model.session;

public class LoginSession {
    private String token;
    private String level;
    private long userId;
    private String username;
    private String password;

    public LoginSession(String token, String level, long userId, String username, String password) {
        this.token = token;
        this.level = level;
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
