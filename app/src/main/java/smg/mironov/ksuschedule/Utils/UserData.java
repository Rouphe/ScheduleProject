package smg.mironov.ksuschedule.Utils;

import smg.mironov.ksuschedule.Models.User;

public class UserData {
    private static UserData instance;
    private User user;

    private UserData() {}

    public static synchronized UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
