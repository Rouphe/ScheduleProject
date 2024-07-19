package smg.mironov.ksuschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
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

/**
 * Класс LoginActivity отвечает за процесс входа в приложение.
 * Он проверяет учетные данные пользователя, сохраняет токен аутентификации и получает данные пользователя.
 *
 * @author Егор Гришанов, Алекснадр Миронов
 *
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity {

    /** Поле для ввода email */
    private EditText emailEdiText;
    /** Поле для ввода пароля */
    private EditText passwordEditText;
    /** Кнопка входа */
    private TextView loginButton;
    /** Ссылка на регистрацию */
    private TextView toRegistration;
    /** Чекбокс для отображения пароля */
    private CheckBox showPasswordCheckbox;
    /** Чекбокс для запоминания пользователя */
    private CheckBox rememberMeCheckbox;

    /** Токен аутентификации */
    private String token;

    /**
     * Метод вызывается при первом создании активности.
     * @param savedInstanceState Если активность пересоздается, этот параметр содержит данные, сохраненные в предыдущем состоянии.
     */
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
        showPasswordCheckbox = findViewById(R.id.show_password);
        rememberMeCheckbox = findViewById(R.id.remember_me);

        loginButton = findViewById(R.id.login_button_text);
        toRegistration = findViewById(R.id.TransferToRegistration);

        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        // Восстановление сохраненных данных для входа
        boolean rememberMe = preferences.getBoolean("remember_me", false);
        rememberMeCheckbox.setChecked(rememberMe);
        if (rememberMe) {
            String savedEmail = preferences.getString("saved_email", "");
            String savedPassword = preferences.getString("saved_password", "");
            emailEdiText.setText(savedEmail);
            passwordEditText.setText(savedPassword);
        }

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

    /**
     * Метод для обработки нажатия кнопки входа.
     * Проверяет корректность введенных данных и отправляет запрос на сервер для аутентификации.
     */
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

    /**
     * Метод для отправки запроса на аутентификацию.
     * @param authRequest объект с данными для аутентификации.
     */
    private void sendAuthResponse(AuthRequest authRequest) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegistrationResponse> call = apiService.login(authRequest);

        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful()) {
                    RegistrationResponse registrationResponse = response.body();

                    // Сохранение токена в SharedPreferences
                    assert registrationResponse != null;
                    token = registrationResponse.getToken();
                    SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("auth_token", token);
                    if (rememberMeCheckbox.isChecked()) {
                        editor.putBoolean("remember_me", true);
                        editor.putString("saved_email", authRequest.getEmail());
                        editor.putString("saved_password", authRequest.getPassword());
                    } else {
                        editor.putBoolean("remember_me", false);
                        editor.remove("saved_email");
                        editor.remove("saved_password");
                    }
                    editor.apply();

                    getUserByEmail(authRequest.getEmail());
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

    /**
     * Метод для получения данных пользователя по email.
     * @param email email пользователя.
     */
    private void getUserByEmail(String email) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUser(token, email);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    saveUserData(user);

                    // Переход на другую активность с задержкой
                    new android.os.Handler().postDelayed(
                            () -> {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            },
                            500
                    );
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

    /**
     * Метод для проверки валидности токена.
     * @param token токен для проверки.
     * @return возвращает true, если токен действителен, иначе false.
     */
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

    /**
     * Метод для сохранения данных пользователя в SharedPreferences.
     * @param user объект с данными пользователя.
     */
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

    /**
     * Метод для загрузки данных пользователя из SharedPreferences.
     */
    private void loadUserData() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        Long userId = preferences.getLong("user_id", 0);
        String userLastName = preferences.getString("user_lastName", null);
        String userFirstName = preferences.getString("user_firstName", null);
        String userMiddleName = preferences.getString("user_middle_name", null);
        String userEmail = preferences.getString("user_email", null);
        String userPassword = preferences.getString("user_password", null);
        String userGroupNumber = preferences.getString("user_group_number", null);
        String userSubgroupNumber = preferences.getString("user_subgroup_number", null);
        String userRole = preferences.getString("user_role", null);

        if (userLastName != null && userFirstName != null && userMiddleName != null &&
                userEmail != null && userPassword != null && userGroupNumber != null &&
                userSubgroupNumber != null && userRole != null) {
            User user = new User(userFirstName, userLastName, userMiddleName, userEmail, userPassword, userGroupNumber, userSubgroupNumber, userRole);
            UserData.getInstance().setUser(user);
        }
    }
}
