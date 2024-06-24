package smg.mironov.ksuschedule.Models;

import java.util.ArrayList;
import java.util.List;

public class DayWeek {

    private int id;
    private String parity;
    private SubgroupDto subgroupDto;
    private TeacherDto teacherDto;
    private SubjectDto subjectDto;
    private String dayWeek;
    private String timeStart;
    private String timeEnd;
    private String classroom;
    private List<Period> additionalPeriods;

    public DayWeek() {
        additionalPeriods = new ArrayList<>();
    }


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

    public SubgroupDto getSubgroup()   {
        return subgroupDto;
    }

    public void setSubgroup(SubgroupDto subgroupDto)    {
        this.subgroupDto = subgroupDto;
    }

    public TeacherDto getTeacher()    {
        return teacherDto;
    }

    public void setTeacher(TeacherDto teacher)    {
        this.teacherDto = teacher;
    }

    public SubjectDto getSubject()    {
        return subjectDto;
    }

    public void setSubject(SubjectDto subjectDto)     {
        this.subjectDto = subjectDto;
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

    public List<Period> getAdditionalPeriods() {
        return additionalPeriods;
    }

    public void setAdditionalPeriods(List<Period> additionalPeriods) {
        this.additionalPeriods = additionalPeriods;
    }

}