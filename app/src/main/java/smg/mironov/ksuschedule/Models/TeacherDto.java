package smg.mironov.ksuschedule.Models;

/**
 * Класс {@link TeacherDto} представляет данные преподавателя.
 * <p>Содержит информацию об идентификаторе преподавателя, его имени, должности и идентификаторе фотографии.</p>
 *
 * @version 1.0
 * @autor
 * Егор Гришанов
 * Александр Миронов
 */
public class TeacherDto {

    /** Идентификатор преподавателя */
    private int id;
    /** Имя преподавателя */
    private String name;
    /** Должность преподавателя */
    private String post;
    /** Идентификатор фотографии */
    private int photoId;

    /**
     * Конструктор для создания объекта {@link TeacherDto}.
     *
     * @param name имя преподавателя
     * @param post должность преподавателя
     */
    public TeacherDto(String name, String post) {
        this.name = name;
        this.post = post;
    }

    // Getters and Setters

    /**
     * Возвращает идентификатор преподавателя.
     *
     * @return идентификатор преподавателя
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор преподавателя.
     *
     * @param id идентификатор преподавателя
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Возвращает имя преподавателя.
     *
     * @return имя преподавателя
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя преподавателя.
     *
     * @param name имя преподавателя
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает должность преподавателя.
     *
     * @return должность преподавателя
     */
    public String getPost() {
        return post;
    }

    /**
     * Устанавливает должность преподавателя.
     *
     * @param post должность преподавателя
     */
    public void setPost(String post) {
        this.post = post;
    }

    /**
     * Возвращает идентификатор фотографии.
     *
     * @return идентификатор фотографии
     */
    public int getPhotoId() {
        return photoId;
    }

    /**
     * Устанавливает идентификатор фотографии.
     *
     * @param photoId идентификатор фотографии
     */
    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }
}
