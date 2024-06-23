package smg.mironov.ksuschedule.Models;

public class Subject {

    private int id;
    private String name;
    private String type;

    //Getters and setters

     public Subject(String name, String type) {
         this.name = name;
         this.type = type;
     }
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

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }
}
