package smg.mironov.ksuschedule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Models.User;
import smg.mironov.ksuschedule.Utils.RegistrationResponse;
import smg.mironov.ksuschedule.Utils.UserData;

public class RegistrationActivity extends AppCompatActivity {

    private EditText surnameEditText, nameEditText, midNameEditText, groupNumberEditText, subgroupNumberEditText, emailEditText, passwordEditText;
    private TextView registerButton, studentRoleTitle, teacherRoleTitle;
    private ImageView studentRole, teacherRole;
    private String selectedRole = "STUDENT";
    private LinearLayout studentRoleBg, teacherRoleBg;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);

        surnameEditText = findViewById(R.id.surname);
        nameEditText = findViewById(R.id.name);
        midNameEditText = findViewById(R.id.mid_name);
        groupNumberEditText = findViewById(R.id.group_number_for_reg);
        subgroupNumberEditText = findViewById(R.id.subgroup_number_for_reg);
        emailEditText = findViewById(R.id.Email);
        passwordEditText = findViewById(R.id.Password);
        registerButton = findViewById(R.id.register_button);
        studentRole = findViewById(R.id.student_role);
        teacherRole = findViewById(R.id.teacher_role);
        studentRoleBg = findViewById(R.id.student);
        teacherRoleBg = findViewById(R.id.teacher);
        teacherRoleTitle = findViewById(R.id.teacherRoleTitle);
        studentRoleTitle = findViewById(R.id.studentRoleTitle);




        studentRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRole = "STUDENT";
                updateRoleSelection();
            }
        });

        teacherRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRole = "TEACHER";
                updateRoleSelection();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });


    }




    private void updateRoleSelection() {
        if ("STUDENT".equals(selectedRole)) {
            studentRole.setColorFilter(Color.parseColor("#DAE2FF"));
            studentRoleBg.setBackgroundResource(R.drawable.custom_registr_semcorners_item);
            studentRoleTitle.setTextColor(Color.parseColor("#DAE2FF"));

            teacherRole.setColorFilter(Color.parseColor("#0229B3"));
            teacherRoleBg.setBackgroundResource(R.drawable.custom_white_semcorners_item);
            teacherRoleTitle.setTextColor(Color.parseColor("#0229B3"));
        } else {
            teacherRole.setColorFilter(Color.parseColor("#DAE2FF"));
            teacherRoleBg.setBackgroundResource(R.drawable.custom_registr_semcorners_item);
            teacherRoleTitle.setTextColor(Color.parseColor("#DAE2FF"));

            studentRole.setColorFilter(Color.parseColor("#0229B3")); // Reset icon color
            studentRoleBg.setBackgroundResource(R.drawable.custom_white_semcorners_item);
            studentRoleTitle.setTextColor(Color.parseColor("#0229B3"));
        }
    }





    private void registerUser() {
        String surname = surnameEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String midName = midNameEditText.getText().toString();
        String groupNumber = groupNumberEditText.getText().toString();
        String subgroupNumber = subgroupNumberEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(surname) || TextUtils.isEmpty(name) || TextUtils.isEmpty(midName) ||
                TextUtils.isEmpty(groupNumber) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Все поля обязательны для заполнения", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(name, surname, midName, email, password, groupNumber, subgroupNumber, selectedRole);
        sendRegistrationRequest(user);
    }

    private void sendRegistrationRequest(User user) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegistrationResponse> call = apiService.register(user);

        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful()) {
                    RegistrationResponse registrationResponse = response.body();
                    //Toast.makeText(RegistrationActivity.this, registrationResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    // Сохранение токена в SharedPreferences
                    assert registrationResponse != null;
                    String token = registrationResponse.getToken();
                    SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("auth_token", token);
                    editor.apply();

                    saveUserData(user);

                    // Переход на другую активность
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegistrationActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(User user) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_lastName", user.getLastName());
        editor.putString("user_firstName", user.getFirstName());
        editor.putString("user_middleName", user.getMiddleName());
        editor.putString("user_email", user.getEmail());
        editor.putString("user_password", user.getPassword());
        editor.putString("user_groupNumber", user.getGroup_number());
        editor.putString("user_subgroupNumber", user.getSubgroup_number());
        editor.putString("user_role", user.getRole());
        editor.apply();
    }
}
