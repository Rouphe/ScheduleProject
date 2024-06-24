package smg.mironov.ksuschedule.Models;

public class TeacherDto {
    private int id;
    private String name;
    private String post;

    public TeacherDto(String name, String post) {

        this.name = name;
        this.post = post;
    }

    //Getter and setters
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPost(){
        return post;
    }

    public void setPost(String post){
        this.post = post;
    }
}
