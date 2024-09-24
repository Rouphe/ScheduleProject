package smg.mironov.ksuschedule.Models;

/**
 * Класс {@link Photo} представляет информацию о фотографии пользователя.
 * <p>Содержит данные об идентификаторе фотографии, URL и объекте пользователя.</p>
 *
 * @version 1.0
 * @author
 * Егор Гришанов
 * Александр Миронов
 */
public class Photo {

    /** Идентификатор фотографии */
    private Long id;
    /** URL фотографии */
    private String url;


    /**
     * Конструктор для создания объекта {@link Photo}.
     *
     * @param id идентификатор фотографии
     * @param url URL фотографии
     */
    public Photo(Long id, String url) {
        this.id = id;
        this.url = url;
    }

    /**
     * Возвращает идентификатор фотографии.
     *
     * @return идентификатор фотографии
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор фотографии.
     *
     * @param id идентификатор фотографии
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает URL фотографии.
     *
     * @return URL фотографии
     */
    public String getUrl() {
        return url;
    }

    /**
     * Устанавливает URL фотографии.
     *
     * @param url URL фотографии
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
