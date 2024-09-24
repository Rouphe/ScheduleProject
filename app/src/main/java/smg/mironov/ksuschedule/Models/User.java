package smg.mironov.ksuschedule.Models;

/**
 * Класс {@link User} представляет данные пользователя.
 * <p>Содержит информацию об идентификаторе пользователя, имени, фамилии, отчестве, email, пароле, номере группы, номере подгруппы, информации, роли, фото и идентификаторе преподавателя.</p>
 *
 * @version 1.0
 * @autor
 * Егор Гришанов
 * Александр Миронов
 */
public class User {

    /** Идентификатор пользователя */
    private Long id;
    /** Имя пользователя */
    private String firstName;
    /** Фамилия пользователя */
    private String lastName;
    /** Отчество пользователя */
    private String middleName;
    /** Email пользователя */
    private String email;
    /** Пароль пользователя */
    private String password;
    /** Номер группы */
    private String group_number;
    /** Номер подгруппы */
    private String subgroup_number;
    /** Информация о пользователе */
    private String info;
    /** Роль пользователя */
    private String role;
    /** Объект фото пользователя */
    private Photo photo;

    /**
     * Конструктор для создания объекта {@link User}.
     *
     * @param firstName имя пользователя
     * @param lastName фамилия пользователя
     * @param middleName отчество пользователя
     * @param email email пользователя
     * @param password пароль пользователя
     * @param group_number номер группы
     * @param subgroup_number номер подгруппы
     * @param role роль пользователя
     */
    public User(
            String firstName,
            String lastName,
            String middleName,
            String email,
            String password,
            String group_number,
            String subgroup_number,
            String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.email = email;
        this.password = password;
        this.group_number = group_number;
        this.subgroup_number = subgroup_number;
        this.role = role;
    }

    /**
     * Конструктор для создания объекта {@link User} без email, пароля, группы и подгруппы.
     *
     * @param firstName имя пользователя
     * @param lastName фамилия пользователя
     * @param middleName отчество пользователя
     */
    public User(String firstName, String lastName, String middleName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
    }

    /**
     * Конструктор для создания объекта {@link User} с информацией.
     *
     * @param firstName имя пользователя
     * @param lastName фамилия пользователя
     * @param middleName отчество пользователя
     * @param info информация о пользователе
     */
    public User(String firstName, String lastName, String middleName, String info) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.info = info;
    }

    /**
     * Конструктор для создания объекта {@link User} с полным набором данных.
     *
     * @param userId идентификатор пользователя
     * @param firstName имя пользователя
     * @param lastName фамилия пользователя
     * @param middleName отчество пользователя
     * @param userEmail email пользователя
     * @param userPassword пароль пользователя
     * @param userGroupNumber номер группы
     * @param userSubgroupNumber номер подгруппы
     * @param userRole роль пользователя
     */
    public User(long userId, String firstName, String lastName, String middleName, String userEmail, String userPassword, String userGroupNumber, String userSubgroupNumber, String userRole) {
        this.id = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.email = userEmail;
        this.password = userPassword;
        this.group_number = userGroupNumber;
        this.subgroup_number = userSubgroupNumber;
        this.role = userRole;
    }

    /**
     * Конструктор для создания объекта {@link User} с полной информацией.
     *
     * @param userId идентификатор пользователя
     * @param firstName имя пользователя
     * @param lastName фамилия пользователя
     * @param middleName отчество пользователя
     * @param userEmail email пользователя
     * @param userPassword пароль пользователя
     * @param userGroupNumber номер группы
     * @param userSubgroupNumber номер подгруппы
     * @param info информация о пользователе
     * @param userRole роль пользователя
     */
    public User(long userId, String firstName, String lastName, String middleName, String userEmail, String userPassword, String userGroupNumber, String userSubgroupNumber, String info, String userRole) {
        this.id = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.email = userEmail;
        this.password = userPassword;
        this.group_number = userGroupNumber;
        this.subgroup_number = userSubgroupNumber;
        this.role = userRole;
        this.info = info;
    }

    /**
     * Метод для авторизации пользователя.
     *
     * @param email email пользователя
     * @param password пароль пользователя
     */
    public void UserAuth(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters

    /**
     * Возвращает идентификатор пользователя.
     *
     * @return идентификатор пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор пользователя.
     *
     * @param id идентификатор пользователя
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param firstName имя пользователя
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Возвращает фамилию пользователя.
     *
     * @return фамилия пользователя
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Устанавливает фамилию пользователя.
     *
     * @param lastName фамилия пользователя
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Возвращает отчество пользователя.
     *
     * @return отчество пользователя
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Устанавливает отчество пользователя.
     *
     * @param middleName отчество пользователя
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Возвращает email пользователя.
     *
     * @return email пользователя
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает email пользователя.
     *
     * @param email email пользователя
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return пароль пользователя
     */
    public String getPassword() {
        return password;
    }

    /**
     * Устанавливает пароль пользователя.
     *
     * @param password пароль пользователя
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Возвращает номер группы.
     *
     * @return номер группы
     */
    public String getGroup_number() {
        return group_number;
    }

    /**
     * Устанавливает номер группы.
     *
     * @param group_number номер группы
     */
    public void setGroup_number(String group_number) {
        this.group_number = group_number;
    }

    /**
     * Возвращает номер подгруппы.
     *
     * @return номер подгруппы
     */
    public String getSubgroup_number() {
        return subgroup_number;
    }

    /**
     * Устанавливает номер подгруппы.
     *
     * @param subgroup_number номер подгруппы
     */
    public void setSubgroup_number(String subgroup_number) {
        this.subgroup_number = subgroup_number;
    }

    /**
     * Возвращает информацию о пользователе.
     *
     * @return информация о пользователе
     */
    public String getInfo() {
        return info;
    }

    /**
     * Устанавливает информацию о пользователе.
     *
     * @param info информация о пользователе
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * Возвращает роль пользователя.
     *
     * @return роль пользователя
     */
    public String getRole() {
        return role;
    }

    /**
     * Устанавливает роль пользователя.
     *
     * @param role роль пользователя
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Возвращает объект фото пользователя.
     *
     * @return объект фото пользователя
     */
    public Photo getPhoto() {
        return photo;
    }

    /**
     * Устанавливает объект фото пользователя.
     *
     * @param photo объект фото пользователя
     */
    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

}
