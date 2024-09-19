package smg.mironov.ksuschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Models.User;

/**
 * Класс {@link EditProfileActivity} отвечает за редактирование профиля пользователя.
 *
 * @author Егор Гришанов, Алекснадр Миронов
 *
 * @version 1.0
 */
public class EditProfileActivity extends AppCompatActivity {

    /** Поле для ввода имени пользователя */
    private EditText userFirstName;
    /** Поле для ввода фамилии пользователя */
    private EditText userLastName;
    /** Поле для ввода отчества пользователя */
    private EditText userMiddleName;
    /** Поле для ввода факультета преподавателя */
    private EditText teachersFaculty;
    /** Поле для ввода кафедры преподавателя */
    private EditText teachersDepartment;
    /** Поле для ввода дополнительной информации преподавателя */
    private EditText teachersInfo;
    /** Кнопка для сохранения изменений */
    private TextView saveButton;
    /** Токен аутентификации */
    private String token;
    /** Контейнер для актуальной информации */
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
        String userInfo = preferences.getString("user_info", null);

        if (Objects.equals(preferences.getString("user_role", null), "TEACHER")){

            if(userInfo != null) {
                String faculty = extractInfo(userInfo, "Факультет:");
                String department = extractInfo(userInfo, "Кафедра:");
                String addInfo = extractInfo(userInfo, "Дополнительная информация:");

                teachersFaculty.setText(faculty);

                teachersDepartment = findViewById(R.id.teacherDepartmentEdit);
                teachersDepartment.setText(department);

                teachersInfo = findViewById(R.id.teachersInfoEdit);
                teachersInfo.setText(addInfo);
            }
        }



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

    /**
     * Метод для сохранения изменений профиля.
     */
    private void saveChanges() {
        String firstName = userFirstName.getText().toString();
        String lastName = userLastName.getText().toString();
        String middleName = userMiddleName.getText().toString();
        String faculty = "";
        String department = "";
        String info = "";


        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        if (Objects.equals(sharedPreferences.getString("user_role", null), "TEACHER")){

            if (faculty != null && department != null && info != null){
                faculty = teachersFaculty.getText().toString();
                department = teachersDepartment.getText().toString();
                info = teachersInfo.getText().toString();
            }

        }

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String informationConstruct = "Факультет: \n" + faculty + "\n" + "Кафедра: \n" + department + "\n" + "Дополнительная информация: \n" + info;
        Log.d("EditProfileActivity", "Information Construct: " + informationConstruct);
        editor.putString("user_info", informationConstruct).apply();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(middleName)) {
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
                    sharedPreferences.getString("user_role", null)
            );

            sendNewInfoStudent(user);
        } else {
            String savedInfo = sharedPreferences.getString("user_info", null);
            Log.d("EditProfileActivity", "Extracted User Info from SharedPreferences: " + savedInfo);
            user = new User(
                    sharedPreferences.getLong("user_id", 0),
                    firstName,
                    lastName,
                    middleName,
                    sharedPreferences.getString("user_email", null),
                    sharedPreferences.getString("user_password", null),
                    sharedPreferences.getString("user_groupNumber", null),
                    sharedPreferences.getString("user_subgroupNumber", null),
                    sharedPreferences.getString("user_role", null)
            );

            user.setInfo(savedInfo);
            Log.d("EditProfileActivity", "User Info: " + user.getInfo());

            sendNewInfoTeacher(user);
        }
    }

    /**
     * Метод для загрузки данных пользователя из {@link SharedPreferences}.
     */
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

    /**
     * Метод для отправки новой информации о студенте на сервер.
     * @param user объект {@link User} с данными пользователя.
     */
    private void sendNewInfoStudent(User user) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.updateStudent(
                token,
                user.getEmail(),
                user.getLastName(),
                user.getFirstName(),
                user.getMiddleName()
        );

        sendUserInfo(call);
    }

    /**
     * Метод для отправки новой информации о преподавателе на сервер.
     * @param user объект {@link User} с данными пользователя.
     */
    private void sendNewInfoTeacher(User user) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.updateTeacher(
                token,
                user.getEmail(),
                user.getLastName(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getInfo()
        );

        Log.d("EditProfileActivity", "Sending User Info to Server: " + user.getInfo());
        sendUserInfo(call);
    }

    /**
     * Метод для отправки информации пользователя на сервер.
     * @param call объект {@link Call} с запросом на сервер.
     */
    private void sendUserInfo(Call<User> call) {
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    refreshTokenIfNeeded(response);
                    User user = response.body();
                    if (user != null) {
                        Log.d("EditProfileActivity", "Server Response User Info: " + user.getInfo());
                        saveUserData(user);
                        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Пустой ответ от сервера", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Ошибка редактирования: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Метод для сохранения данных пользователя в {@link SharedPreferences}.
     * @param user объект {@link User} с данными пользователя.
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
        Log.d("EditProfileActivity", "Saving User Info: " + user.getInfo());
        editor.apply();
    }

    /**
     * Метод для обновления токена аутентификации, если это необходимо.
     * @param response объект {@link Response}, содержащий ответ сервера.
     */
    private void refreshTokenIfNeeded(Response<?> response) {
        // Проверка, есть ли новый токен в заголовке ответа
        String newToken = response.headers().get("Authorization");
        if (newToken != null && newToken.startsWith("Bearer ")) {
            SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("auth_token", newToken.substring(7)); // Сохраняем новый токен без префикса "Bearer "
            editor.apply();
        }
    }

    /**
     * Метод для извлечения информации из строки по заданной метке.
     * @param text текст, из которого нужно извлечь информацию.
     * @param label метка, по которой нужно извлечь информацию.
     * @return строка с извлеченной информацией.
     */
    private String extractInfo(String text, String label) {
        String patternString = label + "\\s*([^\\n]*)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "";
    }
}
