package smg.mironov.ksuschedule.API;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
    Call<User> getUser(@Query("email") String email);

    @GET("/teacher")
    Call<List<TeacherDto>> getAllTeachers();

    @GET("/batches")
    Call<List<GroupDto>> getAllGroups();

    @GET("/subgroup/groupNumber/{group_number}")
    Call<List<SubgroupDto>> getSubgroupsByGroupNumber(@Path("group_number") String group_number);

    @GET("/schedule/subgroup/number/{subgroup_number}")
    Call<List<DayWeek>> getSchedulesBySubgroupNumber(@Path("subgroup_number") String subgroup_number);

    @GET("/schedule/teacher/name/{teacher_name}")
    Call<List<DayWeek>> getSchedulesByTeacherName(@Path("teacher_name") String teacher_name);

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

