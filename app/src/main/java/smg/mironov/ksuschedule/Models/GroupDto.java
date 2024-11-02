package smg.mironov.ksuschedule.Models;

/**
 * Класс {@link GroupDto} представляет данные группы.
 *
 * @author Егор Гришанов, Алекснадр Миронов
 *
 * @version 1.0
 */
public class GroupDto {

    /** Идентификатор группы */
    private int id;
    /** Номер группы */
    private String number;
    /** Направление группы */
    private String direction;
    /** Профиль группы */
    private String profile;

    private Faculty faculty;

    // Getters and Setters

    public Faculty getFacultyDto() {
        return faculty;
    }

    /**
     * Возвращает идентификатор группы.
     *
     * @return идентификатор группы
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор группы.
     *
     * @param id идентификатор группы
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Возвращает номер группы.
     *
     * @return номер группы
     */
    public String getNumber() {
        return number;
    }

    /**
     * Устанавливает номер группы.
     *
     * @param number номер группы
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Возвращает направление группы.
     *
     * @return направление группы
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Устанавливает направление группы.
     *
     * @param direction направление группы
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * Возвращает профиль группы.
     *
     * @return профиль группы
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Устанавливает профиль группы.
     *
     * @param profile профиль группы
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }
}
