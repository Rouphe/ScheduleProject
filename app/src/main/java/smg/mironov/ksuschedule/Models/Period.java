package smg.mironov.ksuschedule.Models;

/**
 * Класс {@link Period} представляет информацию о периоде занятия.
 * <p>Содержит данные о времени начала и окончания занятия, предмете, преподавателе и аудитории.</p>
 *
 * @author Егор Гришанов, Алекснадр Миронов
 *
 * @version 1.0
 */
public class Period {

    /** Время начала занятия */
    private String timeStart;
    /** Время окончания занятия */
    private String timeEnd;
    /** Объект предмета */
    private SubjectDto subjectDto;
    /** Объект преподавателя */
    private TeacherDto teacherDto;
    /** Аудитория */
    private String classroom;

    /**
     * Конструктор для создания объекта {@link Period}.
     *
     * @param timeStart время начала занятия
     * @param timeEnd время окончания занятия
     * @param subjectDto объект предмета
     * @param teacherDto объект преподавателя
     * @param classroom аудитория
     */
    public Period(String timeStart, String timeEnd, SubjectDto subjectDto, TeacherDto teacherDto, String classroom) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.subjectDto = subjectDto;
        this.teacherDto = teacherDto;
        this.classroom = classroom;
    }

    /**
     * Возвращает время начала занятия.
     *
     * @return время начала занятия
     */
    public String getTimeStart() {
        return timeStart;
    }

    /**
     * Устанавливает время начала занятия.
     *
     * @param timeStart время начала занятия
     */
    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    /**
     * Возвращает время окончания занятия.
     *
     * @return время окончания занятия
     */
    public String getTimeEnd() {
        return timeEnd;
    }

    /**
     * Устанавливает время окончания занятия.
     *
     * @param timeEnd время окончания занятия
     */
    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    /**
     * Возвращает объект предмета.
     *
     * @return объект предмета
     */
    public SubjectDto getSubject() {
        return subjectDto;
    }

    /**
     * Устанавливает объект предмета.
     *
     * @param subjectDto объект предмета
     */
    public void setSubject(SubjectDto subjectDto) {
        this.subjectDto = subjectDto;
    }

    /**
     * Возвращает объект преподавателя.
     *
     * @return объект преподавателя
     */
    public TeacherDto getTeacher() {
        return teacherDto;
    }

    /**
     * Устанавливает объект преподавателя.
     *
     * @param teacherDto объект преподавателя
     */
    public void setTeacher(TeacherDto teacherDto) {
        this.teacherDto = teacherDto;
    }

    /**
     * Возвращает аудиторию.
     *
     * @return аудитория
     */
    public String getClassroom() {
        return classroom;
    }

    /**
     * Устанавливает аудиторию.
     *
     * @param classroom аудитория
     */
    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
}
