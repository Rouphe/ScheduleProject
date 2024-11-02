package smg.mironov.ksuschedule.Models;

/**
 * Класс {@link SubgroupDto} представляет данные подгруппы.
 * <p>Содержит информацию об идентификаторе подгруппы, номере и объекте группы.</p>
 *
 * @version 1.0
 * @author
 * Егор Гришанов
 * Александр Миронов
 */
public class SubgroupDto {

    /** Идентификатор подгруппы */
    private int id;
    /** Номер подгруппы */
    private String number;
    /** Объект группы */
    private GroupDto groupDto;

    // Getters and Setters

    /**
     * Возвращает идентификатор подгруппы.
     *
     * @return идентификатор подгруппы
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор подгруппы.
     *
     * @param id идентификатор подгруппы
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Возвращает номер подгруппы.
     *
     * @return номер подгруппы
     */
    public String getNumber() {
        return number;
    }

    /**
     * Устанавливает номер подгруппы.
     *
     * @param number номер подгруппы
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Возвращает объект группы.
     *
     * @return объект группы
     */
    public GroupDto getGroup() {
        return groupDto;
    }

    /**
     * Устанавливает объект группы.
     *
     * @param groupDto объект группы
     */
    public void setGroup(GroupDto groupDto) {
        this.groupDto = groupDto;
    }


}
