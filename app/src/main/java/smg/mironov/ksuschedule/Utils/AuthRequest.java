package smg.mironov.ksuschedule.Utils;

/**
 * Класс для запроса аутентификации пользователя.
 *
 * @version 1.0
 * @authors
 * Егор Гришанов
 * Александр Миронов
 */
public class AuthRequest {

    private String email;
    private String password;

    /**
     * Конструктор для создания объекта запроса аутентификации.
     *
     * @param email    email пользователя
     * @param password пароль пользователя
     */
    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Получение email пользователя.
     *
     * @return email пользователя
     */
    public String getEmail() {
        return email;
    }

    /**
     * Получение пароля пользователя.
     *
     * @return пароль пользователя
     */
    public String getPassword() {
        return password;
    }
}
