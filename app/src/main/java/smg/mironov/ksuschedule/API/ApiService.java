package smg.mironov.ksuschedule.API;


import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import smg.mironov.ksuschedule.Models.DayWeek;
import smg.mironov.ksuschedule.Models.GroupDto;
import smg.mironov.ksuschedule.Models.SubgroupDto;
import smg.mironov.ksuschedule.Models.TeacherDto;
import smg.mironov.ksuschedule.Models.User;
import smg.mironov.ksuschedule.Utils.AuthRequest;
import smg.mironov.ksuschedule.Utils.RegistrationResponse;

public interface ApiService {

    @POST("/api/v1/auth/register")
    Call<RegistrationResponse> register(@Body User user);

    @POST("api/v1/auth/authenticate")
    Call<RegistrationResponse> login(@Body AuthRequest user);



    @GET("/api/v1/user")
    Call<User> getUser(@Header("Authorization") String token, @Query("email") String email);

    @GET("/api/v1/user/teacher/{id}")
    Call<User> getTeacherUserByTeacherId(@Header("Authorization") String token, @Path("id") int teacherId);

    @PUT("/api/v1/user/update/student/{email}")
    Call<User> updateStudent(@Header("Authorization") String token,
                             @Path("email") String email,
                             @Query("lastName") String lastName,
                             @Query("firstName") String firstName,
                             @Query("middleName") String middleName);

    @PUT("/api/v1/user/update/teacher/{email}")
    Call<User> updateTeacher(@Header("Authorization") String token,
                             @Path("email") String email,
                             @Query("lastName") String lastName,
                             @Query("firstName") String firstName,
                             @Query("middleName") String middleName,
                             @Query("info") String info
                             );

    @Multipart
    @POST("/api/photos/upload/{email}")
    Call<ResponseBody> uploadProfileImage(
            @Header("Authorization") String token,
            @Path("email") String email,
            @Part MultipartBody.Part image
    );

    @GET("/api/photos/download/email")
    Call <ResponseBody> getProfileImage ( @Header("Authorization") String token,
                                          @Query("email") String email
    );


    @GET("/api/photos/download/id")
    Call<ResponseBody> getTeacherPhoto (
            @Header("Authorization") String token,
            @Query("id") int id
    );

    @GET("/teacher")
    Call<List<TeacherDto>> getAllTeachers();

    @GET("/batches")
    Call<List<GroupDto>> getAllGroups();

    @GET("/teacher/name/{name}")
    Call <TeacherDto> getTeacherByName (@Header("Authorization") String token, @Path("name") String name);

    @GET("/teacher/id/{id}")
    Call <TeacherDto> getTeacherById (@Header("Authorization") String token, @Path("id") int id);

    @GET("/batches/number/{number}")
    Call <GroupDto> getGroupByNumber(@Header("Authorization") String token, @Path("number") String group_number);

    @GET("/subgroup/groupNumber/{group_number}")
    Call<List<SubgroupDto>> getSubgroupsByGroupNumber(@Header("Authorization") String token, @Path("group_number") String group_number);

    @GET("/api/v1/schedule/subgroup/number/{subgroup_number}")
    Call<List<DayWeek>> getSchedulesBySubgroupNumber(@Header("Authorization") String token, @Path("subgroup_number") String subgroup_number);

    @GET("/api/v1/schedule/teacher/name/{teacher_name}")
    Call<List<DayWeek>> getSchedulesByTeacherName(@Header("Authorization") String token, @Path("teacher_name") String teacher_name);

    @GET("/schedule/parity/{parity}")
    Call<List<DayWeek>> getSchedulesByParity(@Path("parity") String parity);

    @GET("/schedule/subject/{subject_name}")
    Call<List<DayWeek>> getSchedulesBySubjectName(@Path("subject_name") String subject_name);

    @GET("/schedule/filter/subgroup_number{subgroup_number}/type/{type}")
    Call<List<DayWeek>> getSchedulesBySubgroupNumberAndType (@Path("subgroup_number") String subgroup_number, @Path("type") String type);

    @GET("/schedule/filter/subgroup_number{subgroup_number}/teacher_name/{teacher_name}")
    Call<List<DayWeek>> getSchedulesBySubgroupNumberAndTeacherName (@Path("subgroup_number") String subgroup_number, @Path("teacher_name") String teacher_name);

    @GET("/schedule/filter/subgroup_number/{subgroup_number}/subject_name/{subject_name}")
    Call<List<DayWeek>> getSchedulesBySubgroupNumberAndSubjectName (@Path("subgroup_number") String subgroup_number, @Path("subject_name") String subject_view);





}

