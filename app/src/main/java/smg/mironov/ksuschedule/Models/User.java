package smg.mironov.ksuschedule.Models;

public class User {

    private Long id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String email;

    private String password;

    private String group_number;

    private String subgroup_number;

    private String info;

    private String role;

    private Photo photo;

    public User (
                 String firstName,
                 String lastName,
                 String middleName,
                 String email,
                 String password,
                 String group_number,
                 String subgroup_number,
                 String role){
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.email = email;
        this.password = password;
        this.group_number = group_number;
        this.subgroup_number = subgroup_number;
        this.role = role;
    }

    public void UserAuth(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName(){
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroup_number() {
        return group_number;
    }

    public void setGroup_number(String group_number){
        this.group_number = group_number;
    }

    public String getSubgroup_number() {
        return subgroup_number;
    }

    public void setSubgroup_number (String subgroup_number) {
        this.subgroup_number = subgroup_number;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }




}
