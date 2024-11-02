package smg.mironov.ksuschedule.API;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import smg.mironov.ksuschedule.Models.DayWeek;
import smg.mironov.ksuschedule.Models.Faculty;
import smg.mironov.ksuschedule.Models.GroupDto;
import smg.mironov.ksuschedule.Models.SubgroupDto;
import smg.mironov.ksuschedule.Models.TeacherDto;
import smg.mironov.ksuschedule.Models.User;
import smg.mironov.ksuschedule.Utils.AuthRequest;
import smg.mironov.ksuschedule.Utils.PasswordResetRequest;
import smg.mironov.ksuschedule.Utils.RegistrationResponse;

/**
 * Интерфейс для определения методов API.
 *
 * @version 1.0
 * @authors Егор Гришанов
 * Александр Миронов
 */
public interface ApiService {

    @GET("/faculty")
    Call<List<Faculty>> getAllFaculties();

    @GET("/batches/faculty/name/{name}")
    Call<List<GroupDto>> getGroupsByFaculty(@Path("name") String name);

    /**
     * Регистрация пользователя.
     *
     * @param user объект пользователя
     * @return объект {@link Call} с {@link RegistrationResponse}
     */
    @POST("/api/v1/auth/register")
    Call<RegistrationResponse> register(@Body User user);

    /**
     * Аутентификация пользователя.
     *
     * @param user объект запроса аутентификации
     * @return объект {@link Call} с {@link RegistrationResponse}
     */
    @POST("/api/v1/auth/authenticate")
    Call<RegistrationResponse> login(@Body AuthRequest user);

    /**
     * Запрос на сброс пароля
     *
     * @param resetRequest
     * @return
     */
    @POST("/api/password-reset/request")
    Call<Void> requestPasswordReset(@Body PasswordResetRequest resetRequest);

    /**
     * Получение данных пользователя по email.
     *
     * @param token токен авторизации
     * @param email email пользователя
     * @return объект {@link Call} с {@link User}
     */
    @GET("/api/v1/user")
    Call<User> getUser(@Header("Authorization") String token, @Query("email") String email);

    /**
     * Получение данных преподавателя по ID.
     *
     * @param token     токен авторизации
     * @param teacherId ID преподавателя
     * @return объект {@link Call} с {@link User}
     */
    @GET("/api/v1/user/teacher/{id}")
    Call<User> getTeacherUserByTeacherId(@Header("Authorization") String token, @Path("id") int teacherId);

    /**
     * Обновление данных студента.
     *
     * @param token      токен авторизации
     * @param email      email пользователя
     * @param lastName   фамилия пользователя
     * @param firstName  имя пользователя
     * @param middleName отчество пользователя
     * @return объект {@link Call} с {@link User}
     */
    @PUT("/api/v1/user/update/student/{email}")
    Call<User> updateStudent(@Header("Authorization") String token,
                             @Path("email") String email,
                             @Query("lastName") String lastName,
                             @Query("firstName") String firstName,
                             @Query("middleName") String middleName);

    /**
     * Обновление данных преподавателя.
     *
     * @param token      токен авторизации
     * @param email      email пользователя
     * @param lastName   фамилия пользователя
     * @param firstName  имя пользователя
     * @param middleName отчество пользователя
     * @param info       информация о пользователе
     * @return объект {@link Call} с {@link User}
     */
    @PUT("/api/v1/user/update/teacher/{email}")
    Call<User> updateTeacher(@Header("Authorization") String token,
                             @Path("email") String email,
                             @Query("lastName") String lastName,
                             @Query("firstName") String firstName,
                             @Query("middleName") String middleName,
                             @Query("info") String info);

    /**
     * Загрузка изображения профиля.
     *
     * @param token токен авторизации
     * @param email email пользователя
     * @param image изображение профиля
     * @return объект {@link Call} с {@link ResponseBody}
     */
    @Multipart
    @POST("/api/photos/upload/{email}")
    Call<ResponseBody> uploadProfileImage(
            @Header("Authorization") String token,
            @Path("email") String email,
            @Part MultipartBody.Part image
    );

    /**
     * Получение изображения профиля по email.
     *
     * @param token токен авторизации
     * @param id id фотографии
     * @return объект {@link Call} с {@link ResponseBody}
     */
    @GET("/api/photos/download/id")
    Call<ResponseBody> getProfileImage(@Header("Authorization") String token,
                                       @Query("id") Long id);

    /**
     * Получение изображения профиля по ID.
     *
     * @param token токен авторизации
     * @param id    ID пользователя
     * @return объект {@link Call} с {@link ResponseBody}
     */
    @GET("/api/photos/download/id")
    Call<ResponseBody> getUserTeacherPhoto(
            @Header("Authorization") String token,
            @Query("id") Long id
    );

    /**
     * Получение фотографии преподавателя по ID.
     *
     * @param token     токен авторизации
     * @param teacherId ID преподавателя
     * @return объект {@link Call} с {@link ResponseBody}
     */
    @GET("/api/photos/download/teacher/{id}")
    Call<ResponseBody> getTeacherPhoto(@Header("Authorization") String token, @Path("id") int teacherId);

    /**
     * Получение всех преподавателей.
     *
     * @return объект {@link Call} с списком {@link TeacherDto}
     */
    @GET("/api/v1/teacher")
    Call<List<TeacherDto>> getAllTeachers();

    /**
     * Получение всех групп.
     *
     * @return объект {@link Call} с списком {@link GroupDto}
     */
    @GET("/batches")
    Call<List<GroupDto>> getAllGroups();

    /**
     * Получение данных о преподавателе по имени.
     *
     * @param name имя преподавателя
     * @return объект {@link Call} с {@link TeacherDto}
     */
    @GET("/api/v1/teacher/name/{name}")
    Call<TeacherDto> getTeacherByName(@Path("name") String name);

    /**
     * Получение данных о преподавателе по ID.
     *
     * @param token токен авторизации
     * @param id    ID преподавателя
     * @return объект {@link Call} с {@link TeacherDto}
     */
    @GET("/api/v1/teacher/id/{id}")
    Call<TeacherDto> getTeacherById(@Header("Authorization") String token, @Path("id") int id);

    /**
     * Получение данных о группе по номеру.
     *
     * @param token        токен авторизации
     * @param group_number номер группы
     * @return объект {@link Call} с {@link GroupDto}
     */
    @GET("/batches/number/{number}")
    Call<GroupDto> getGroupByNumber(@Path("number") String group_number);

    /**
     * Получение подгрупп по номеру группы.
     *
     * @param group_number номер группы
     * @return объект {@link Call} с списком {@link SubgroupDto}
     */
    @GET("/api/v1/subgroup/groupNumber/{group_number}")
    Call<List<SubgroupDto>> getSubgroupsByGroupNumber(@Path("group_number") String group_number);

    /**
     * Получение расписания по номеру подгруппы.
     *
     * @param token           токен авторизации
     * @param subgroup_number номер подгруппы
     * @return объект {@link Call} с списком {@link DayWeek}
     */
    @GET("/api/v1/schedule/subgroup/number/{subgroup_number}")
    Call<List<DayWeek>> getSchedulesBySubgroupNumber(@Header("Authorization") String token, @Path("subgroup_number") String subgroup_number);

    /**
     * Получение расписания по имени преподавателя.
     *
     * @param token        токен авторизации
     * @param teacher_name имя преподавателя
     * @return объект {@link Call} с списком {@link DayWeek}
     */
    @GET("/api/v1/schedule/teacher/name/{teacher_name}")
    Call<List<DayWeek>> getSchedulesByTeacherName(@Header("Authorization") String token, @Path("teacher_name") String teacher_name);

    /**
     * Получение расписания по четности недели.
     *
     * @param parity четность недели
     * @return объект {@link Call} с списком {@link DayWeek}
     */
    @GET("/schedule/parity/{parity}")
    Call<List<DayWeek>> getSchedulesByParity(@Path("parity") String parity);

    /**
     * Получение расписания по имени предмета.
     *
     * @param subject_name имя предмета
     * @return объект {@link Call} с списком {@link DayWeek}
     */
    @GET("/schedule/subject/{subject_name}")
    Call<List<DayWeek>> getSchedulesBySubjectName(@Path("subject_name") String subject_name);

    /**
     * Получение расписания по номеру подгруппы и типу занятия.
     *
     * @param subgroup_number номер подгруппы
     * @param type            тип занятия
     * @return объект {@link Call} с списком {@link DayWeek}
     */
    @GET("/schedule/filter/subgroup_number{subgroup_number}/type/{type}")
    Call<List<DayWeek>> getSchedulesBySubgroupNumberAndType(@Path("subgroup_number") String subgroup_number, @Path("type") String type);

    /**
     * Получение расписания по номеру подгруппы и имени преподавателя.
     *
     * @param subgroup_number номер подгруппы
     * @param teacher_name    имя преподавателя
     * @return объект {@link Call} с списком {@link DayWeek}
     */
    @GET("/schedule/filter/subgroup_number{subgroup_number}/teacher_name/{teacher_name}")
    Call<List<DayWeek>> getSchedulesBySubgroupNumberAndTeacherName(@Path("subgroup_number") String subgroup_number, @Path("teacher_name") String teacher_name);

    /**
     * Получение расписания по номеру подгруппы и имени предмета.
     *
     * @param subgroup_number номер подгруппы
     * @param subject_view    имя предмета
     * @return объект {@link Call} с списком {@link DayWeek}
     */
    @GET("/schedule/filter/subgroup_number/{subgroup_number}/subject_name/{subject_name}")
    Call<List<DayWeek>> getSchedulesBySubgroupNumberAndSubjectName(@Path("subgroup_number") String subgroup_number, @Path("subject_name") String subject_view);
    @PUT("/api/v1/user/update/student/groups/{email}")
    Call<Void> updateUserGroupAndFaculty(@Header("Authorization") String token, @Path("email")  String userEmail, @Query("groupNumber") String groupNumber, @Query("subgroupNumber")String subgroupNumber, @Query("facultyName") String facultyName);
}
