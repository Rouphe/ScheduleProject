package smg.mironov.ksuschedule.Models;

public class Period {
    private String timeStart;
    private String timeEnd;
    private SubjectDto subjectDto;
    private TeacherDto teacherDto;
    private String classroom;

    // Конструкторы, геттеры и сеттеры

    public Period(String timeStart, String timeEnd, SubjectDto subjectDto, TeacherDto teacherDto, String classroom) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.subjectDto = subjectDto;
        this.teacherDto = teacherDto;
        this.classroom = classroom;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public SubjectDto getSubject() {
        return subjectDto;
    }

    public void setSubject(SubjectDto subjectDto) {
        this.subjectDto = subjectDto;
    }

    public TeacherDto getTeacher() {
        return teacherDto;
    }

    public void setTeacher(TeacherDto teacherDto) {
        this.teacherDto = teacherDto;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
}

