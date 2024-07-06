package smg.mironov.ksuschedule.Utils;

public class AuthRequest {

    String email;

    String password;

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
}
