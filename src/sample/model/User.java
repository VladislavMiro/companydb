package sample.model;

public class User {
    private final int id;
    private final String login;
    private final UserRole userRole;

    public User(int id, String login, UserRole userRole) {
        this.id = id;
        this.login = login;
        this.userRole = userRole;
    }

    public int getId() { return id; }

    public String getLogin() { return login; }

    public UserRole getUserRole() { return userRole; }

}
