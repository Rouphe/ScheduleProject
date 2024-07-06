package smg.mironov.ksuschedule.Models;

public class Photo {

    private Long id;

    private String url;

    private User user;

    public Photo(Long id, String url, User user){
        this.id = id;
        this.url = url;
        this.user = user;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
