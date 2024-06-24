package smg.mironov.ksuschedule.Models;

public class GroupDto {

    private int id;
    private String number;
    private String direction;
    private String profile;

    //Getters and setters

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getNumber(){
        return number;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public String getDirection(){
        return direction;
    }

    public void setDirection(String direction){
        this.direction = direction;
    }

    public String getProfile(){
        return profile;
    }

    public void setProfile(){
        this.profile = profile;
    }
}
