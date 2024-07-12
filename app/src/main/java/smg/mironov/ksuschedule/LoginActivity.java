package smg.mironov.ksuschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Models.User;
import smg.mironov.ksuschedule.Utils.AuthRequest;
import smg.mironov.ksuschedule.Utils.RegistrationResponse;
import smg.mironov.ksuschedule.Utils.UserData;

// TODO: баг при регистрации -> входе в этот же аккаунт

public class LoginActivity extends AppCompatActivity {

    private EditText emailEdiText, passwordEditText;
    private TextView loginButton, toRegistration;

    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_screen);



        // Проверка токена
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = preferences.getString("auth_token", null);
        if (token != null && isTokenValid(token)) {
            loadUserData();

            // Токен действителен, перенаправление в MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }



        emailEdiText = findViewById(R.id.Login);
        passwordEditText = findViewById(R.id.Password);

        loginButton = findViewById(R.id.login_button_text);
        toRegistration = findViewById(R.id.TransferToRegistration);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        toRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void login() {
        String email = emailEdiText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Все поля обязательны для заполнения", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthRequest authRequest = new AuthRequest(email, password);
        sendAuthResponse(authRequest);

    }


    private void sendAuthResponse(AuthRequest authRequest) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegistrationResponse> call = apiService.login(authRequest);

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

                    getUserByEmail(authRequest.getEmail());
                    // Переход на другую активность
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Ошибка входа", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getUserByEmail(String email) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUser(token, email);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    if (user != null) {

                        // Сохранение данных пользователя в SharedPreferences
                        saveUserData(user);

                        // Дополнительные действия с полученными данными пользователя
                    } else {
                        Toast.makeText(LoginActivity.this, "Данные пользователя не получены", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Ошибка получения данных пользователя", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isTokenValid(String token) {
        try {
            String[] parts = token.split("\\."); // Разделение токена на части
            if (parts.length != 3) {
                return false;
            }

            String payload = new String(Base64.decode(parts[1], Base64.DEFAULT));
            JSONObject jsonObject = new JSONObject(payload);
            long exp = jsonObject.getLong("exp");

            // Проверка срока действия
            long currentTime = System.currentTimeMillis() / 1000;
            return exp > currentTime;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


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
        editor.apply();
    }

    private void loadUserData() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        Long userId = preferences.getLong("user_id", 0);
        String userLastName = preferences.getString("user_lastName", null);
        String userFirstName = preferences.getString("user_firstName", null);
        String userMiddleName = preferences.getString("user_middleName", null);
        String userEmail = preferences.getString("user_email", null);
        String userPassword = preferences.getString("user_password", null);
        String userGroupNumber = preferences.getString("user_groupNumber", null);
        String userSubgroupNumber = preferences.getString("user_subgroupNumber", null);
        String userRole = preferences.getString("user_role", null);

        if (userLastName != null && userFirstName != null && userMiddleName != null &&
            userEmail != null && userPassword != null && userGroupNumber != null &&
            userSubgroupNumber != null && userRole != null) {
            User user = new User(userFirstName, userLastName, userMiddleName, userEmail, userPassword, userGroupNumber, userSubgroupNumber, userRole);
            user.setLastName(userLastName);
            user.setFirstName(userFirstName);
            user.setMiddleName(userMiddleName);
            user.setEmail(userEmail);
            user.setPassword(userPassword);
            user.setGroup_number(userGroupNumber);
            user.setSubgroup_number(userSubgroupNumber);
            user.setRole(userRole);
            // Установите другие поля, если они есть

            UserData.getInstance().setUser(user);
        }
    }
}
