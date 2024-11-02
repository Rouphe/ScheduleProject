package smg.mironov.ksuschedule.Models;

public class Faculty {

    private Integer id;
    private String facultyName;
    private String abbreviation;

    public Integer getId() {
        return id;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return facultyName; // или любое другое поле, представляющее название факультета
    }

}
