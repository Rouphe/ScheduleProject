package smg.mironov.ksuschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import smg.mironov.ksuschedule.Utils.AuthRequest;
import smg.mironov.ksuschedule.Utils.RegistrationResponse;
import smg.mironov.ksuschedule.Models.User;
import smg.mironov.ksuschedule.Utils.UserData;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView loginButton;
    private CheckBox showPasswordCheckbox;
    private CheckBox rememberMeCheckbox;
    private TextView toRegistration;
    private TextView toReset;

    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_screen);

        // Инициализация элементов
        emailEditText = findViewById(R.id.Login);
        passwordEditText = findViewById(R.id.Password);
        showPasswordCheckbox = findViewById(R.id.show_password);
        rememberMeCheckbox = findViewById(R.id.remember_me);
        loginButton = findViewById(R.id.login_button_text);
        toRegistration = findViewById(R.id.TransferToRegistration);
        toReset = findViewById(R.id.toReset);

        // Проверка и загрузка сохраненных данных
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String savedEmail = preferences.getString("saved_email", null);
        String savedPassword = preferences.getString("saved_password", null);
        token = preferences.getString("auth_token", null);

        Intent intent = getIntent();
        boolean fromMainActivity = intent.getBooleanExtra("from_main_activity", false);

        if (token != null && isTokenValid(token)) {
            loadUserData();
            navigateToMainActivity();
            return;
        } else if (fromMainActivity && savedEmail != null && savedPassword != null) {
            AuthRequest authRequest = new AuthRequest(savedEmail, savedPassword);
            sendAuthResponse(authRequest);
            return;
        } else if (savedEmail != null && savedPassword != null) {
            emailEditText.setText(savedEmail);
            passwordEditText.setText(savedPassword);
        }

        // Настройка поведения чекбокса отображения пароля
        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        // Логика нажатия на кнопку входа
        loginButton.setOnClickListener(v -> login());

        // Переход к регистрации
        toRegistration.setOnClickListener(v -> {
            Intent intentToRegister = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intentToRegister);
            finish();
        });

        // Переход к сбросу пароля
        toReset.setOnClickListener(v -> {
            Intent intentToReset = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intentToReset);
            finish();
        });
    }


    private void login() {
        String email = emailEditText.getText().toString();
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

                    // Сохранение токена и данных пользователя
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

    private void getUserByEmail(String email) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUser(token, email);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    saveUserData(user);
                    navigateToMainActivity();
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
        SharedPreferences preferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
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
            UserData.getInstance().setUser(user);
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isTokenValid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            String payload = new String(android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT));
            JSONObject jsonObject = new JSONObject(payload);
            long exp = jsonObject.getLong("exp");

            long currentTime = System.currentTimeMillis() / 1000;
            return exp > currentTime;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
