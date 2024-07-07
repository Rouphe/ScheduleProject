package smg.mironov.ksuschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Models.GroupDto;
import smg.mironov.ksuschedule.Models.TeacherDto;

public class ProfileActivity extends AppCompatActivity {

    private TextView userLastname, userFirstName, userMiddleName, userGroupNumber, userGroupDirection, userGroupProfile;
    private View additionalInfoContainer;
    private ImageView chevronDown, logoutButton;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        token = "Bearer " + preferences.getString("auth_token", null);

        // Настройка кнопок навигационной панели
        ImageView navButton1 = findViewById(R.id.teachers_icon);
        ImageView navButton2 = findViewById(R.id.home_icon);


        userLastname = findViewById(R.id.userLastname);
        userFirstName = findViewById(R.id.userFirstName);
        userMiddleName = findViewById(R.id.userMiddleName);
        userGroupNumber = findViewById(R.id.userGroupNumber);
        additionalInfoContainer = findViewById(R.id.additional_info_container);
        chevronDown = findViewById(R.id.chevron_down);
        userGroupDirection = findViewById(R.id.userGroupDirection);
        userGroupProfile = findViewById(R.id.userGroupProfile);
        logoutButton = findViewById(R.id.logout_icon);

        loadUserData();


        chevronDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAdditionalInfo();
            }
        });

        navButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика переключения на первый экран
                switchToTeachersScreen();
            }
        });

        navButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика переключения на второй экран
                switchToMain();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void switchToTeachersScreen() {
        // Логика переключения на экран преподавателей
        Intent intent = new Intent(this, TeachersActivity.class);
        startActivity(intent);
    }

    private void switchToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        String lastName = sharedPreferences.getString("user_lastName", "Фамилия");
        String firstName = sharedPreferences.getString("user_firstName", "Имя");
        String middleName = sharedPreferences.getString("user_middleName", "Отчество");
        String groupNumber = sharedPreferences.getString("user_groupNumber", "100");
        String role = sharedPreferences.getString("user_role", "STUDENT");

        userLastname.setText(lastName);
        userFirstName.setText(firstName);
        userMiddleName.setText(middleName);
        userGroupNumber.setText(groupNumber);

        if (role.equals("STUDENT")) {
            userGroupNumber.setText(groupNumber);
            fetchGroupInfo(groupNumber);
        } else {
            userGroupNumber.setVisibility(View.INVISIBLE);
            fetchTeacherInfo(lastName + " " + firstName.charAt(0) + "." + middleName.charAt(0) + ".");
        }
    }

    private void toggleAdditionalInfo() {
        if (additionalInfoContainer.getVisibility() == View.GONE) {
            additionalInfoContainer.setVisibility(View.VISIBLE);
            chevronDown.setImageResource(R.drawable.chevron_up);
        } else {
            additionalInfoContainer.setVisibility(View.GONE);
            chevronDown.setImageResource(R.drawable.chevron_down_white);
        }
    }

    private void fetchGroupInfo(String groupNumber) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call <GroupDto> call = apiService.getGroupByNumber(token, groupNumber);

        call.enqueue(new Callback<GroupDto>() {
            @Override
            public void onResponse(Call<GroupDto> call, Response<GroupDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GroupDto groupDto = response.body();

                    userGroupDirection.setText(groupDto.getDirection());
                    userGroupProfile.setText(groupDto.getProfile());
                }
            }

            @Override
            public void onFailure(Call<GroupDto> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Ошибка загрузки информации", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTeacherInfo(String name) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<TeacherDto> call = apiService.getTeacherByName(token, name);

        call.enqueue(new Callback<TeacherDto>() {
            @Override
            public void onResponse(Call<TeacherDto> call, Response<TeacherDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TeacherDto teacherDto = response.body();

                    TextView groupLabel = findViewById(R.id.group_label);
                    groupLabel.setText("Должность");
                    userGroupNumber.setText(teacherDto.getPost());
                    userGroupNumber.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<TeacherDto> call, Throwable t) {

            }
        });
    }


}
