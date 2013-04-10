package spring.examples.spel.model;

public class User {
    private String name;

    public User(String user) {
        this.name = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String user) {
        this.name = user;
    }
}