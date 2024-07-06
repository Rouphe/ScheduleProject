package smg.mironov.ksuschedule;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private TextView registerButton;
    private ImageView studentRole, teacherRole;
    private String selectedRole = "STUDENT";
    private LinearLayout studentRoleBg, teacherRoleBg;

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

        studentRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRole = "STUDENT";
                //updateRoleSelection();
            }
        });

        teacherRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRole = "TEACHER";
                //updateRoleSelection();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

    }

//    private void updateRoleSelection() {
//        if ("STUDENT".equals(selectedRole)) {
//            studentRole.setColorFilter(Color.parseColor("#DAE2FF"));
//            studentRoleBg.setBackgroundResource(R.drawable.custom_registr_semcorners_item);
//        } else {
//            teacherRole.setColorFilter(Color.parseColor("#DAE2FF"));
//            teacherRole.setBackgroundResource(R.drawable.custom_registr_semcorners_item);
//        }
//    }


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

                    UserData.getInstance().setUser(user);

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
}
