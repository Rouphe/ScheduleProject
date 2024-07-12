package smg.mironov.ksuschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Models.User;
import smg.mironov.ksuschedule.Utils.RegistrationResponse;

public class EditProfileActivity extends AppCompatActivity {

    private EditText userFirstName, userLastName, userMiddleName, teachersFaculty, teachersDepartment, teachersInfo;
    private TextView saveButton;
    private String token;
    private LinearLayout actual_information;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editing_screen);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        token = "Bearer " + preferences.getString("auth_token", null);

        userFirstName = findViewById(R.id.userFirstNameEdit);
        userLastName = findViewById(R.id.userLastNameEdit);
        userMiddleName = findViewById(R.id.userMiddleNameEdit);
        teachersFaculty = findViewById(R.id.teachersFacultyEdit);
        teachersDepartment = findViewById(R.id.teacherDepartmentEdit);
        teachersInfo = findViewById(R.id.teachersInfoEdit);
        saveButton = findViewById(R.id.saveButtonText);
        actual_information = findViewById(R.id.actual_information);

        loadUserData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

    }

    private void saveChanges() {
        String firstName = userFirstName.getText().toString();
        String lastName = userLastName.getText().toString();
        String middleName = userMiddleName.getText().toString();
        String faculty = teachersFaculty.getText().toString();
        String department = teachersDepartment.getText().toString();
        String info = teachersInfo.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String information_construct = "Факультет: \n" + faculty + "\n" + "Кафедра: \n" + department + "\n" + "Дополнительная иформация: \n" + info;


        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(middleName)){
            Toast.makeText(this, "Все поля обязательны для заполнения", Toast.LENGTH_SHORT).show();
            return;
        }

        User user;
        if ("STUDENT".equals(sharedPreferences.getString("user_role", "STUDENT"))) {
            user = new User(
                    sharedPreferences.getLong("user_id", 0),
                    firstName,
                    lastName,
                    middleName,
                    sharedPreferences.getString("user_email", null),
                    sharedPreferences.getString("user_password", null),
                    sharedPreferences.getString("user_groupNumber", null),
                    sharedPreferences.getString("user_subgroupNumber", null),
                    sharedPreferences.getString("user_role", null));

            sendNewInfoStudent();
        } else {
            user = new User(sharedPreferences.getLong("user_id", 0),
                    firstName,
                    lastName,
                    middleName,
                    sharedPreferences.getString("user_email", null),
                    sharedPreferences.getString("user_password", null),
                    sharedPreferences.getString("user_groupNumber", null),
                    sharedPreferences.getString("user_subgroupNumber", null),
                    editor.putString("user_info", information_construct).toString(),
                    sharedPreferences.getString("user_role", null));



            sendNewInfoTeacher();
        }


    }




    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        String lastName = sharedPreferences.getString("user_lastName", "Фамилия");
        String firstName = sharedPreferences.getString("user_firstName", "Имя");
        String middleName = sharedPreferences.getString("user_middleName", "Отчество");
        String role = sharedPreferences.getString("user_role", "STUDENT");

        userLastName.setText(lastName);
        userFirstName.setText(firstName);
        userMiddleName.setText(middleName);




        if (role.equals("STUDENT")) {
            actual_information.setVisibility(View.GONE);
        }
    }

    private void sendNewInfoStudent() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.updateStudent(token,
                sharedPreferences.getString("user_email", null),
                sharedPreferences.getString("user_lastName", null),
                sharedPreferences.getString("user_firstName", null),
                sharedPreferences.getString("user_middleName", null));

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    //Toast.makeText(RegistrationActivity.this, registrationResponse.getMessage(), Toast.LENGTH_SHORT).show();


                    assert user != null;
                    saveUserData(user);

                    // Переход на другую активность
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Ошибка редактирования", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNewInfoTeacher() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.updateTeacher(token,
                sharedPreferences.getString("user_email", null),
                sharedPreferences.getString("user_lastName", null),
                sharedPreferences.getString("user_firstName", null),
                sharedPreferences.getString("user_middleName", null),
                sharedPreferences.getString("user_info", null));

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    //Toast.makeText(RegistrationActivity.this, registrationResponse.getMessage(), Toast.LENGTH_SHORT).show();


                    saveUserData(user);

                    // Переход на другую активность
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(User user) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("user_id", user.getId());
        editor.putString("user_lastName", user.getLastName());
        editor.putString("user_firstName", user.getFirstName());
        editor.putString("user_middleName", user.getMiddleName());
        editor.putString("user_email", user.getEmail());
        editor.putString("user_password", user.getPassword());
        editor.putString("user_groupNumber", user.getGroup_number());
        editor.putString("user_subgroupNumber", user.getSubgroup_number());
        editor.putString("user_role", user.getRole());
        editor.putString("user_info", user.getInfo());
        editor.apply();
    }
}
