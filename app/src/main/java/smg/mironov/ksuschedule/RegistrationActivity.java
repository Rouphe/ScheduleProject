package smg.mironov.ksuschedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.my.tracker.MyTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Models.Faculty;
import smg.mironov.ksuschedule.Models.GroupDto;
import smg.mironov.ksuschedule.Models.SubgroupDto;
import smg.mironov.ksuschedule.Models.User;
import smg.mironov.ksuschedule.Utils.RegistrationResponse;

/**
 * Класс {@link RegistrationActivity} отвечает за процесс регистрации пользователя.
 * Он позволяет пользователю выбрать роль, ввести личные данные и отправить их на сервер для регистрации.
 *
 * @author Егор Гришанов, Алекснадр Миронов
 *
 * @version 1.0
 */
public class RegistrationActivity extends AppCompatActivity {

    /** Поле для ввода фамилии */
    private EditText surnameEditText;
    /** Поле для ввода имени */
    private EditText nameEditText;
    /** Поле для ввода отчества */
    private EditText midNameEditText;
    /** Поле для ввода номера группы */
    private EditText groupNumberEditText;
    /** Поле для ввода номера подгруппы */
    private EditText subgroupNumberEditText;
    /** Поле для ввода email */
    private EditText emailEditText;
    /** Поле для ввода пароля */
    private EditText passwordEditText;
    /** Кнопка регистрации */
    private TextView registerButton;
    /** Заголовок для роли студента */
    private TextView studentRoleTitle;
    /** Заголовок для роли преподавателя */
    private TextView teacherRoleTitle;
    /** Иконка для роли студента */
    private ImageView studentRole;
    /** Иконка для роли преподавателя */
    private ImageView teacherRole;
    /** Выбранная роль */
    private String selectedRole = "STUDENT";
    /** Фон для роли студента */
    private LinearLayout studentRoleBg;
    /** Фон для роли преподавателя */
    private LinearLayout teacherRoleBg;
    /** Чекбокс для отображения пароля */
    private CheckBox showPasswordCheckbox;

    private Spinner subgroupSpinner;
    private Spinner facultySpinner;
    private Spinner groupSpinner;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);

        surnameEditText = findViewById(R.id.surname);
        nameEditText = findViewById(R.id.name);
        midNameEditText = findViewById(R.id.mid_name);
        //groupNumberEditText = findViewById(R.id.group_number_for_reg);
        //subgroupNumberEditText = findViewById(R.id.subgroup_number_for_reg);
        subgroupSpinner = findViewById(R.id.subgroup_number_for_reg);
        emailEditText = findViewById(R.id.Email);
        passwordEditText = findViewById(R.id.Password);
        registerButton = findViewById(R.id.register_button);
        studentRole = findViewById(R.id.student_role);
        teacherRole = findViewById(R.id.teacher_role);
        studentRoleBg = findViewById(R.id.student);
        teacherRoleBg = findViewById(R.id.teacher);
        teacherRoleTitle = findViewById(R.id.teacherRoleTitle);
        studentRoleTitle = findViewById(R.id.studentRoleTitle);
        facultySpinner = findViewById(R.id.faculty);
        groupSpinner = findViewById(R.id.group_for_reg);

        fetchFaculties();



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

        facultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedFaculty = facultySpinner.getSelectedItem().toString();
                fetchGroups(selectedFaculty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle if needed
            }
        });

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedGroup = groupSpinner.getSelectedItem().toString();
                fetchSubgroups(selectedGroup);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle if needed
            }
        });


    }

    private void fetchGroups(String facultyName) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<GroupDto>> call = apiService.getGroupsByFaculty(facultyName); // Assuming your API returns groups by faculty

        call.enqueue(new Callback<List<GroupDto>>() {
            @Override
            public void onResponse(Call<List<GroupDto>> call, Response<List<GroupDto>> response) {
                if (response.isSuccessful()) {
                    List<GroupDto> groups = response.body();
                    if (groups != null) {
                        populateGroupSpinner(groups);
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Ошибка получения групп", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GroupDto>> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void fetchSubgroups(String groupNumber) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<SubgroupDto>> call = apiService.getSubgroupsByGroupNumber(groupNumber); // Assuming the response is a list of subgroups

        call.enqueue(new Callback<List<SubgroupDto>>() {
            @Override
            public void onResponse(Call<List<SubgroupDto>> call, Response<List<SubgroupDto>> response) {
                if (response.isSuccessful()) {
                    List<SubgroupDto> subgroups = response.body();
                    if (subgroups != null) {
                        populateSubgroupSpinner(subgroups);
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Ошибка получения подгрупп", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubgroupDto>> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFaculties() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Faculty>> call = apiService.getAllFaculties();

        call.enqueue(new Callback<List<Faculty>>() {
            @Override
            public void onResponse(Call<List<Faculty>> call, Response<List<Faculty>> response) {
                if (response.isSuccessful()){
                    List<Faculty> faculties = response.body();
                    if (faculties != null){
                        populateFacultySpinner(faculties);
                    }
                }
                else {
                    Toast.makeText(RegistrationActivity.this, "Ошибка получения факультетов", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Faculty>> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFacultySpinner(List<Faculty> faculties) {
        ArrayAdapter<Faculty> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, faculties);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_reg);
        facultySpinner.setAdapter(adapter);
    }


    private void populateSubgroupSpinner(List<SubgroupDto> subgroups) {
        List<String> subgroupNumbers = new ArrayList<>();
        for (SubgroupDto subgroup : subgroups) {
            subgroupNumbers.add(subgroup.getNumber());
        }

        // Используйте уже созданный subgroupSpinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, subgroupNumbers);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_reg);
        subgroupSpinner.setAdapter(adapter);
    }

    private void populateGroupSpinner(List<GroupDto> groups) {
        List<String> groupNumbers = new ArrayList<>();
        for (GroupDto group : groups) {
            groupNumbers.add(group.getNumber());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, groupNumbers);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_reg);
        groupSpinner.setAdapter(adapter);
    }



    /**
     * Метод для обновления отображения выбранной роли.
     */
    private void updateRoleSelection() {
        if ("STUDENT".equals(selectedRole)) {
            studentRole.setColorFilter(Color.parseColor("#DAE2FF"));
            studentRoleBg.setBackgroundResource(R.drawable.custom_registr_semcorners_item);
            studentRoleTitle.setTextColor(Color.parseColor("#DAE2FF"));

            groupSpinner.setVisibility(View.VISIBLE);
            subgroupSpinner.setVisibility(View.VISIBLE);
            facultySpinner.setVisibility(View.VISIBLE);

            teacherRole.setColorFilter(Color.parseColor("#0229B3"));
            teacherRoleBg.setBackgroundResource(R.drawable.custom_white_semcorners_item);
            teacherRoleTitle.setTextColor(Color.parseColor("#0229B3"));
        } else {
            teacherRole.setColorFilter(Color.parseColor("#DAE2FF"));
            teacherRoleBg.setBackgroundResource(R.drawable.custom_registr_semcorners_item);
            teacherRoleTitle.setTextColor(Color.parseColor("#DAE2FF"));

            groupSpinner.setVisibility(View.GONE);
            subgroupSpinner.setVisibility(View.GONE);
            facultySpinner.setVisibility(View.GONE);

            studentRole.setColorFilter(Color.parseColor("#0229B3")); // Сброс цвета иконки
            studentRoleBg.setBackgroundResource(R.drawable.custom_white_semcorners_item);
            studentRoleTitle.setTextColor(Color.parseColor("#0229B3"));
        }
    }

    /**
     * Метод для обработки нажатия кнопки регистрации.
     * Проверяет корректность введенных данных и отправляет запрос на сервер для регистрации.
     */
    private void registerUser() {
        String surname = surnameEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String midName = midNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(surname) || TextUtils.isEmpty(name) || TextUtils.isEmpty(midName) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Все поля обязательны для заполнения", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создаем пользователя в зависимости от выбранной роли
        User user;
        if ("STUDENT".equals(selectedRole)) {
            String selectedGroupNumber = (String) groupSpinner.getSelectedItem();
            String selectedSubgroupNumber = (String) subgroupSpinner.getSelectedItem();
            Faculty faculty = (Faculty) facultySpinner.getSelectedItem();
            user = new User(name, surname, midName, email, password, selectedGroupNumber, selectedSubgroupNumber, faculty, selectedRole);
        } else {
            user = new User(name, surname, midName, email, password, null, null, selectedRole);
        }

        sendRegistrationRequest(user);
    }

    /**
     * Метод для отправки запроса на регистрацию пользователя.
     * @param user объект {@link User} с данными пользователя.
     */
    private void sendRegistrationRequest(User user) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegistrationResponse> call = apiService.register(user);

        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful()) {
                    RegistrationResponse registrationResponse = response.body();

                    // Сохранение токена в SharedPreferences
                    assert registrationResponse != null;
                    String token = registrationResponse.getToken();
                    long userId = registrationResponse.getUserId(); // Получение user_id
                    SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("auth_token", token);
                    editor.putLong("user_id", userId); // Сохранение user_id
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

    /**
     * Метод для сохранения данных пользователя в {@link SharedPreferences}.
     * @param user объект {@link User} с данными пользователя.
     */
    private void saveUserData(User user) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (Objects.equals(user.getRole(), "TEACHER")){
            editor.putString("user_lastName", user.getLastName().trim());
            editor.putString("user_firstName", user.getFirstName().trim());
            editor.putString("user_middleName", user.getMiddleName().trim());
            editor.putString("user_email", user.getEmail().trim());
            editor.putString("user_password", user.getPassword().trim());
            editor.putString("user_role", user.getRole().trim());
            editor.apply();
        }
        else {
            editor.putString("user_lastName", user.getLastName().trim());
            editor.putString("user_firstName", user.getFirstName().trim());
            editor.putString("user_middleName", user.getMiddleName().trim());
            editor.putString("user_email", user.getEmail().trim());
            editor.putString("user_password", user.getPassword().trim());
            editor.putString("user_groupNumber", user.getGroup_number().trim());
            editor.putString("user_subgroupNumber", user.getSubgroup_number().trim());
            editor.putString("user_faculty", user.getFacultyDto().getFacultyName().trim());
            editor.putString("user_role", user.getRole().trim());
            editor.apply();
        }

    }
}
