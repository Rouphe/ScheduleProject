package smg.mironov.ksuschedule.Models;

public class Period {
    private String timeStart;
    private String timeEnd;
    private Subject subject;
    private Teacher teacher;
    private String classroom;

    // Конструкторы, геттеры и сеттеры

    public Period(String timeStart, String timeEnd, Subject subject, Teacher teacher, String classroom) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.subject = subject;
        this.teacher = teacher;
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

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
}

