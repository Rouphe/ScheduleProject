package smg.mironov.ksuschedule;

import java.util.List;

public class DayWeek {

//fvdgfdvfhudfnk
    private int id;
    private String parity;
    private Subgroup subgroup;
    private Teacher teacher;
    private Subject subject;
    private String dayWeek;
    private String timeStart;
    private String timeEnd;
    private String classroom;


    //Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id)  {
        this.id = id;
    }

    public String getParity()  {
        return parity;
    }

    public void setParity(String parity)   {
        this.parity = parity;
    }

    public Subgroup getSubgroup()   {
        return subgroup;
    }

    public void setSubgroup(Subgroup subgroup)    {
        this.subgroup = subgroup;
    }

    public Teacher getTeacher()    {
        return teacher;
    }

    public void setTeacher(Teacher teacher)    {
        this.teacher = teacher;
    }

    public Subject getSubject()    {
        return subject;
    }

    public void setSubject(Subject subject)     {
        this.subject = subject;
    }

    public String getDayWeek()    {
        return dayWeek;
    }

    public void setDayWeek(String dayWeek)      {
        this.dayWeek = dayWeek;
    }

    public String getTimeStart()     {
        return timeStart;
    }

    public void setTimeStart(String timeStart)      {
        this.timeStart = timeStart;
    }

    public String getTimeEnd()      {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd)       {
        this.timeEnd = timeEnd;
    }

    public String getClassroom()     {
        return classroom;
    }

    public void setClassroom(String classroom)     {
        this.classroom = classroom;
    }

}