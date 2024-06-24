package smg.mironov.ksuschedule.Models;

public class SubgroupDto {
    private int id;
    private String number;
    private GroupDto groupDto;

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

    public GroupDto getGroup(){
        return groupDto;
    }

    public void setGroup(GroupDto groupDto){
        this.groupDto = groupDto;
    }
}

