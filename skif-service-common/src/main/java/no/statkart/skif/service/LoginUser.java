package no.statkart.skif.service;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class LoginUser {
    private String username;
    private String password;

    public LoginUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

