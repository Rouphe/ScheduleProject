package smg.mironov.ksuschedule.Utils;

/**
 * Класс для обработки ответа при регистрации пользователя.
 *
 * @version 1.0
 * @authors
 * Егор Гришанов
 * Александр Миронов
 */
public class RegistrationResponse {

    private String token;

    /**
     * Получение токена из ответа на регистрацию.
     *
     * @return токен в виде строки
     */
    public String getToken() {
        return token;
    }
}
