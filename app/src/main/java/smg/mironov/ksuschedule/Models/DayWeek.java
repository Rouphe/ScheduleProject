package smg.mironov.ksuschedule.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс {@link DayWeek} представляет расписание на определенный день недели.
 * <p>Содержит информацию о парности недели, подгруппе, преподавателе, предмете, времени начала и окончания занятия, аудитории и дополнительных периодах.</p>
 *
 * @author Егор Гришанов, Алекснадр Миронов
 *
 * @version 1.0
 */
public class DayWeek {

    /** Идентификатор дня недели */
    private int id;
    /** Парность недели */
    private String parity;
    /** Объект подгруппы */
    private SubgroupDto subgroupDto;
    /** Объект преподавателя */
    private TeacherDto teacherDto;
    /** Объект предмета */
    private SubjectDto subjectDto;
    /** День недели */
    private String dayWeek;
    /** Время начала занятия */
    private String timeStart;
    /** Время окончания занятия */
    private String timeEnd;
    /** Аудитория */
    private String classroom;
    /** Список дополнительных периодов */
    private List<Period> additionalPeriods;

    /**
     * Конструктор по умолчанию.
     */
    public DayWeek() {
        additionalPeriods = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DayWeek dayWeek = (DayWeek) o;
        return Objects.equals(this.dayWeek, dayWeek.dayWeek) &&
                Objects.equals(this.timeStart, dayWeek.timeStart) &&
                Objects.equals(this.timeEnd, dayWeek.timeEnd) &&
                Objects.equals(this.parity, dayWeek.parity) &&
                Objects.equals(this.subjectDto, dayWeek.subjectDto) &&
                Objects.equals(this.teacherDto, dayWeek.teacherDto) &&
                Objects.equals(this.classroom, dayWeek.classroom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayWeek, timeStart, timeEnd, parity, subjectDto, teacherDto, classroom);
    }

    // Getters and Setters

    /**
     * Возвращает идентификатор дня недели.
     *
     * @return идентификатор дня недели
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор дня недели.
     *
     * @param id идентификатор дня недели
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Возвращает парность недели.
     *
     * @return парность недели
     */
    public String getParity() {
        return parity;
    }

    /**
     * Устанавливает парность недели.
     *
     * @param parity парность недели
     */
    public void setParity(String parity) {
        this.parity = parity;
    }

    /**
     * Возвращает объект подгруппы.
     *
     * @return объект подгруппы
     */
    public SubgroupDto getSubgroup() {
        return subgroupDto;
    }

    /**
     * Устанавливает объект подгруппы.
     *
     * @param subgroupDto объект подгруппы
     */
    public void setSubgroup(SubgroupDto subgroupDto) {
        this.subgroupDto = subgroupDto;
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
     * @param teacher объект преподавателя
     */
    public void setTeacher(TeacherDto teacher) {
        this.teacherDto = teacher;
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
     * Возвращает день недели.
     *
     * @return день недели
     */
    public String getDayWeek() {
        return dayWeek;
    }

    /**
     * Устанавливает день недели.
     *
     * @param dayWeek день недели
     */
    public void setDayWeek(String dayWeek) {
        this.dayWeek = dayWeek;
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

    /**
     * Возвращает список дополнительных периодов.
     *
     * @return список дополнительных периодов
     */
    public List<Period> getAdditionalPeriods() {
        return additionalPeriods;
    }

    /**
     * Устанавливает список дополнительных периодов.
     *
     * @param additionalPeriods список дополнительных периодов
     */
    public void setAdditionalPeriods(List<Period> additionalPeriods) {
        this.additionalPeriods = additionalPeriods;
    }

    /**
     * Возвращает объект группы, связанный с подгруппой.
     *
     * @return объект группы
     */
    public GroupDto getGroup() {
        return subgroupDto.getGroup();
    }
}
