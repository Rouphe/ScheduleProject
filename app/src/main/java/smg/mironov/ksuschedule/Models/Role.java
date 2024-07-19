package smg.mironov.ksuschedule.Models;

/**
 * Перечисление {@link Role} представляет роли пользователей в системе.
 * <p>Содержит следующие роли: STUDENT, ADMIN, TEACHER, MONITOR.</p>
 *
 * @version 1.0
 * @author
 * Егор Гришанов
 * Александр Миронов
 */
public enum Role {

    /** Роль студента */
    STUDENT,

    /** Роль администратора */
    ADMIN,

    /** Роль преподавателя */
    TEACHER,

    /** Роль старосты */
    MONITOR
}
