package smg.mironov.ksuschedule.API;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import smg.mironov.ksuschedule.Models.DayWeek;
import smg.mironov.ksuschedule.Models.Group;
import smg.mironov.ksuschedule.Models.Subgroup;
import smg.mironov.ksuschedule.Models.Teacher;

public interface ApiService {

    @GET("/teacher")
    Call<List<Teacher>> getAllTeachers();

    @GET("/batches")
    Call<List<Group>> getAllGroups();

    @GET("/subgroup/groupNumber/{group_number}")
    Call<List<Subgroup>> getSubgroupsByGroupNumber(@Path("group_number") int group_number);

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

