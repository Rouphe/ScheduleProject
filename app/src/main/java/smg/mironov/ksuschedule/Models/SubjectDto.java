package smg.mironov.ksuschedule.Models;

/**
 * Класс {@link SubjectDto} представляет данные предмета.
 * <p>Содержит информацию об идентификаторе предмета, его названии и типе.</p>
 *
 * @version 1.0
 * @autor Егор Гришанов
 * Александр Миронов
 */
public class SubjectDto {

    /**
     * Идентификатор предмета
     */
    private int id;
    /**
     * Название предмета
     */
    private String name;
    /**
     * Тип предмета
     */
    private String type;

    /**
     * Конструктор для создания объекта {@link SubjectDto}.
     *
     * @param name название предмета
     * @param type тип предмета
     */
    public SubjectDto(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // Getters and Setters

    /**
     * Возвращает идентификатор предмета.
     *
     * @return идентификатор предмета
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор предмета.
     *
     * @param id идентификатор предмета
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Возвращает название предмета.
     *
     * @return название предмета
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает название предмета.
     *
     * @param name название предмета
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает тип предмета.
     *
     * @return тип предмета
     */
    public String getType() {
        return type;
    }

    /**
     * Устанавливает тип предмета.
     *
     * @param type тип предмета
     */
    public void setType(String type) {
        this.type = type;
    }
}
