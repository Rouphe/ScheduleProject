package smg.mironov.ksuschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

    private String userEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editing_screen);



        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        token = "Bearer " + preferences.getString("auth_token", null);
        userEmail = preferences.getString("user_email", null); // Получите email пользователя

        userFirstName = findViewById(R.id.userFirstNameEdit);
        userLastName = findViewById(R.id.userLastNameEdit);
        userMiddleName = findViewById(R.id.userMiddleNameEdit);
        teachersFaculty = findViewById(R.id.teachersFacultyEdit);
        teachersDepartment = findViewById(R.id.teacherDepartmentEdit); // Initialize here
        teachersInfo = findViewById(R.id.teachersInfoEdit); // Initialize here
        saveButton = findViewById(R.id.saveButtonText);
        actual_information = findViewById(R.id.actual_information);
        String userInfo = preferences.getString("user_info", null);

        // Загружаем данные пользователя из сервера
        loadUserDataFromServer();

        if (Objects.equals(preferences.getString("user_role", null), "TEACHER")) {
            if (userInfo != null) {
                String faculty = extractInfo(userInfo, "Факультет:");
                String department = extractInfo(userInfo, "Кафедра:");
                String addInfo = extractInfo(userInfo, "Дополнительная информация:");

                teachersFaculty.setText(faculty);
                teachersDepartment.setText(department); // Set text only if userInfo is available
                teachersInfo.setText(addInfo); // Set text only if userInfo is available

                actual_information.setVisibility(View.VISIBLE);
            }
        }
        else {
            actual_information.setVisibility(View.GONE);
        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        userFirstName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    userLastName.requestFocus(); // Переход к следующему полю
                    return true;
                }
                return false;
            }
        });

        userLastName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    userMiddleName.requestFocus(); // Переход к следующему полю
                    return true;
                }
                return false;
            }
        });

        userMiddleName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Закрыть клавиатуру или выполнить другое действие
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

    }

    private void loadUserDataFromServer() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Предполагаем, что у вас есть переменная userEmail, содержащая email пользователя
        Call<User> call = apiService.getUser(token, userEmail); // вызываем API метод

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    updateUIWithUserData(user);
                } else {
                    Toast.makeText(EditProfileActivity.this, "Ошибка загрузки данных пользователя", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithUserData(User user) {
        // Устанавливаем информацию в соответствующие поля
        userFirstName.setText(user.getFirstName());
        userLastName.setText(user.getLastName());
        userMiddleName.setText(user.getMiddleName());

        if ("TEACHER".equals(user.getRole()) && user.getInfo() != null) {
            String faculty = extractInfo(user.getInfo(), "Факультет:");
            String department = extractInfo(user.getInfo(), "Кафедра:");
            String addInfo = extractInfo(user.getInfo(), "Дополнительная информация:");

            // Вставляем данные только если они не пустые
            if (!TextUtils.isEmpty(faculty)) {
                teachersFaculty.setText(faculty);
            }
            if (!TextUtils.isEmpty(department)) {
                teachersDepartment.setText(department);
            }
            if (!TextUtils.isEmpty(addInfo)) {
                teachersInfo.setText(addInfo);
            }
        }
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

        String informationConstruct = "";

        if (!TextUtils.isEmpty(faculty)) {
            informationConstruct += "Факультет: \n" + faculty + "\n";
        }
        if (!TextUtils.isEmpty(department)) {
            informationConstruct += "Кафедра: \n" + department + "\n";
        }
        if (!TextUtils.isEmpty(info)) {
            informationConstruct += "Дополнительная информация: \n" + info;
        }

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
