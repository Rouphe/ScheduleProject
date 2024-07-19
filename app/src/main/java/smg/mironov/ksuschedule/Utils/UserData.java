package smg.mironov.ksuschedule.Utils;

import smg.mironov.ksuschedule.Models.User;

/**
 * Класс для хранения данных пользователя.
 *
 * @version 1.0
 * @authors
 * Егор Гришанов
 * Александр Миронов
 */
public class UserData {
    private static UserData instance;
    private User user;

    private UserData() {}

    /**
     * Получение единственного экземпляра класса {@link UserData}.
     *
     * @return экземпляр класса {@link UserData}
     */
    public static synchronized UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    /**
     * Установка данных пользователя.
     *
     * @param user объект {@link User}, представляющий данные пользователя
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Получение данных пользователя.
     *
     * @return объект {@link User}, представляющий данные пользователя
     */
    public User getUser() {
        return user;
    }
}
