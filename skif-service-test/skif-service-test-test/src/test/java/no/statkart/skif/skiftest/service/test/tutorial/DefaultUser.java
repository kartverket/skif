package no.statkart.skif.skiftest.service.test.tutorial;

public class DefaultUser implements User {
    private String username;

    @Override
    public String getUsername() {
        return username;
    }

    public DefaultUser setUsername(String username) {
        this.username = username;
        return this;
    }

}
