package smg.mironov.ksuschedule;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.my.tracker.MyTracker;
import com.my.tracker.MyTrackerConfig;
import com.my.tracker.MyTrackerParams;

import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Adapters.DayWeekAdapter;
import smg.mironov.ksuschedule.Models.DayWeek;
import smg.mironov.ksuschedule.Models.SubgroupDto;
import smg.mironov.ksuschedule.Models.User;
import smg.mironov.ksuschedule.Utils.NetworkUtils;


/**
 * Класс {@link MainActivity} отвечает за главный экран приложения, где отображается расписание пользователя.
 *
 * @author Егор Гришанов, Алекснадр Миронов
 *
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    /** RecyclerView для отображения расписания */
    private RecyclerView recyclerView;
    /** Адаптер для RecyclerView */
    private DayWeekAdapter scheduleAdapter;
    /** Spinner для выбора подгруппы */
    private Spinner subgroupSpinner;
    /** Экземпляр {@link ApiService} для выполнения сетевых запросов */
    private ApiService apiService;
    /** Адаптер для Spinner */
    private ArrayAdapter<String> spinnerAdapter;
    /** TextView для отображения четности недели */
    private TextView parity;
    /** Объект {@link User} для хранения данных пользователя */
    private User user;
    private DayWeekAdapter dayWeekAdapter;
    /** Токен аутентификации */
    private String token;

    private LinearLayout serverError;
    /** Объект для работы с SharedPreferences */
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {



        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        user = loadUserData();

        if (user == null && !isTokenValid(token)) {
            logout();
        }

        // Получаем SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Проверяем, какая тема была выбрана
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);

        // При необходимости, настройте конфигурацию трекера
        MyTrackerParams trackerParams = MyTracker.getTrackerParams();
        MyTrackerConfig trackerConfig = MyTracker.getTrackerConfig();
        // Настройте параметры трекера
        trackerParams.setCustomParam("android_id", getAndroidId(getApplicationContext()));
        trackerParams.setCustomUserId(String.valueOf(sharedPreferences.getLong("user_id", 1)));
        trackerConfig.setLaunchTimeout(60);
        trackerConfig.setAutotrackingPurchaseEnabled(false);
        // Инициализируйте трекер
        String SDK_KEY = "76249747832706624692";
        MyTracker.initTracker(SDK_KEY, getApplication());
        MyTracker.trackLaunchManually(this);

        setContentView(R.layout.main_screen);
        serverError = findViewById(R.id.server_error);


        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        token = "Bearer " + preferences.getString("auth_token", null);

        saveCurrentParity();


        Spinner editTextWeek = findViewById(R.id.editTextWeek);
        spinnerAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item_settings, new String[]{"ЧИСЛИТЕЛЬ", "ЗНАМЕНАТЕЛЬ"});
        editTextWeek.setAdapter(spinnerAdapter);

        // Устанавливаем значение Spinner из SharedPreferences
        String savedParity = sharedPreferences.getString("parity", null);
        if (savedParity != null) {
            int spinnerPosition = spinnerAdapter.getPosition(savedParity);
            editTextWeek.setSelection(spinnerPosition);
        }





        TextView groupNumberTextView = findViewById(R.id.group_number);
        if (user != null) {
            if (Objects.equals(user.getRole(), "TEACHER")) {
                // Для учителя прячем поле с номером группы
                groupNumberTextView.setVisibility(View.INVISIBLE);

                TextView teacherName = findViewById(R.id.Group);
                teacherName.setText(user.getLastName() + " " + user.getFirstName());
            } else if (Objects.equals(user.getRole(), "STUDENT")) {
                if (user.getGroup_number() != null) {
                    groupNumberTextView.setText(user.getGroup_number());
                } else {
                    Log.e("MainActivity", "User group number is null");
                    Toast.makeText(this, "Ошибка загрузки данных пользователя", Toast.LENGTH_SHORT).show();
                    logout();  // Переход на логин, если группа не найдена для студента
                }
            } else {
                Log.e("MainActivity", "Unknown user role: " + user.getRole());
                Toast.makeText(this, "Ошибка загрузки данных пользователя", Toast.LENGTH_SHORT).show();
                logout();
            }
        } else {
            Log.e("MainActivity", "User is null");
            Toast.makeText(this, "Ошибка загрузки данных пользователя", Toast.LENGTH_SHORT).show();
            logout();
        }




        subgroupSpinner = findViewById(R.id.Subgroup);


        if (NetworkUtils.isNetworkConnected(this) && user != null){
            if (user.getRole().equals("TEACHER")) {
                subgroupSpinner.setVisibility(View.INVISIBLE);
            }
        }
        else {
            logout();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация адаптера
        scheduleAdapter = new DayWeekAdapter(this);
        recyclerView.setAdapter(scheduleAdapter);

        // Инициализация кнопок навигационной панели
        ImageView navButton1 = findViewById(R.id.teachers_icon);
        ImageView navButton2 = findViewById(R.id.profile_icon);



        subgroupSpinner = findViewById(R.id.Subgroup);
//        spinnerAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, new ArrayList<>());
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        subgroupSpinner.setAdapter(spinnerAdapter);

        spinnerAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, new ArrayList<>());
        // Устанавливаем разметку для выпадающего списка
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        subgroupSpinner.setAdapter(spinnerAdapter);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        dayWeekAdapter = new DayWeekAdapter(this);
        recyclerView.setAdapter(dayWeekAdapter);



        apiService = ApiClient.getClient().create(ApiService.class);

        if (NetworkUtils.isNetworkConnected(this) || user != null){
            if (user.getRole().equals("STUDENT")){
                fetchSubgroups();
            }
        }
        else {
            logout();
        }


        navButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика переключения на первый экран
                switchToScreen1();
            }
        });

        navButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика переключения на второй экран
                switchToScreen2();
            }
        });



        subgroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSubgroup = (String) parent.getItemAtPosition(position);
                String savedParity = sharedPreferences.getString("parity", null);
                changeSubgroup(selectedSubgroup); // Сохранение выбранной подгруппы
                // Вызов метода для загрузки расписания по новой подгруппе
                fetchScheduleFromServer(savedParity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        editTextWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = (String) parent.getItemAtPosition(position);
                changeParity(selectedValue);
                fetchScheduleFromServer(selectedValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (NetworkUtils.isNetworkConnected(this)){
            // Получение данных с сервера
            fetchScheduleFromServer(savedParity);
        }
        else {
            logout();
        }
        // Получение всех значений
        Map<String, ?> allEntries = sharedPreferences.getAll();

        // Вывод в логи
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferences", entry.getKey() + ": " + entry.getValue().toString());
        }

    }

    // Метод получения Android ID
    static @Nullable String getAndroidId(Context context)
    {
        try
        {
            ContentResolver cr = context.getContentResolver();
            if (cr != null)
            {
                return Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
            }
        }
        catch (Throwable e) {}

        return null;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Ваш код, который будет выполнен при перезапуске активности
        Log.d("MainActivity", "Activity was restarted");
        //logout();
    }



    private void saveCurrentParity() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
        boolean isEvenWeek = startOfWeek.get(WeekFields.ISO.weekOfWeekBasedYear()) % 2 == 0;
        String currentWeekParity = isEvenWeek ? "ЧИСЛИТЕЛЬ" : "ЗНАМЕНАТЕЛЬ";

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("parity", currentWeekParity);
        editor.apply();
    }
    LocalDate today = LocalDate.now();
    LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
    boolean isEvenWeek = startOfWeek.get(WeekFields.ISO.weekOfWeekBasedYear()) % 2 == 0;
    String currentWeekParity = isEvenWeek ? "ЧИСЛИТЕЛЬ" : "ЗНАМЕНАТЕЛЬ";




    private void updateParityUI() {
        SharedPreferences updatedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String currentParity = updatedPreferences.getString("parity", currentWeekParity);
        parity.setText(currentParity);
    }

    /**
     * Метод для загрузки данных пользователя из {@link SharedPreferences}.
     * @return объект {@link User} с данными пользователя.
     */
    private User loadUserData() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String lastName = preferences.getString("user_lastName", null);
        String firstName = preferences.getString("user_firstName", null);
        String middleName = preferences.getString("user_middleName", null);
        String email = preferences.getString("user_email", null);
        String faculty = preferences.getString("user_faculty", null);
        String role = preferences.getString("user_role", null);
        String groupNumber = preferences.getString("user_groupNumber", null);
        String subgroupNumber = preferences.getString("user_subgroupNumber", null);
        String password = preferences.getString("user_password", null);

        if (lastName == null || email == null || role == null) {
            return null;
        }

        return new User(firstName, lastName, middleName, email, password, groupNumber, subgroupNumber, role);
    }

    /**
     * Метод для получения подгрупп с сервера.
     */
    private void fetchSubgroups() {
        String savedGroupNumber = user.getGroup_number();
        Call<List<SubgroupDto>> call = apiService.getSubgroupsByGroupNumber(savedGroupNumber);
        call.enqueue(new Callback<List<SubgroupDto>>() {
            @Override
            public void onResponse(Call<List<SubgroupDto>> call, Response<List<SubgroupDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    refreshTokenIfNeeded(response);

                    List<SubgroupDto> subgroups = response.body();
                    List<String> subgroupNumbers = new ArrayList<>();
                    for (SubgroupDto subgroup : subgroups) {
                        subgroupNumbers.add(subgroup.getNumber());
                    }
                    spinnerAdapter.clear();
                    spinnerAdapter.addAll(subgroupNumbers);
                    spinnerAdapter.notifyDataSetChanged();

                    // Установить сохраненное значение подгруппы после загрузки данных с сервера
                    String savedSubgroup = user.getSubgroup_number();
                    if (savedSubgroup != null) {
                        int subgroupPosition = spinnerAdapter.getPosition(savedSubgroup);
                        if (subgroupPosition >= 0) {
                            subgroupSpinner.setSelection(subgroupPosition);
                        }
                    }
                }
                else {
                    logout();
                }
            }

            @Override
            public void onFailure(Call<List<SubgroupDto>> call, Throwable t) {
                logout();
            }
        });
    }

    /**
     * Метод для получения расписания с сервера.
     */
    private void fetchScheduleFromServer(String newParity) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String savedSubgroupNumber = user.getSubgroup_number();
        String role = user.getRole();

        Log.d("MainActivity", "Fetching schedule for role: " + role);

        Callback<List<DayWeek>> callback = new Callback<List<DayWeek>>() {
            @Override
            public void onResponse(Call<List<DayWeek>> call, Response<List<DayWeek>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    refreshTokenIfNeeded(response);
                    List<DayWeek> scheduleList = response.body();

                    // Убираем ошибку сервера, показываем расписание
                    serverError.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    dayWeekAdapter.setFilterParity(newParity);
                    dayWeekAdapter.updateScheduleList(scheduleList);
                    Set<DayWeek> uniqueScheduleSet = new HashSet<>(scheduleList);
                    List<DayWeek> uniqueScheduleList = new ArrayList<>(uniqueScheduleSet);
                    scheduleAdapter.updateScheduleList(uniqueScheduleList);
                    Log.d("MainActivity", "Schedule loaded successfully");
                } else {
                    // Ошибка - показываем плашку с ошибкой сервера и скрываем расписание
                    recyclerView.setVisibility(View.GONE);
                    serverError.setVisibility(View.VISIBLE);
                    logout();
                    Log.e("MainActivity", "Error loading schedule, showing server error message");
                }
            }

            @Override
            public void onFailure(Call<List<DayWeek>> call, Throwable t) {
                Log.e("MainActivity", "Error fetching schedule", t);
                recyclerView.setVisibility(View.GONE);
                serverError.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
            }
        };

        if (Objects.equals(user.getRole(), "STUDENT")) {
            Call<List<DayWeek>> call = apiService.getSchedulesBySubgroupNumber(token, savedSubgroupNumber);
            call.enqueue(callback);
        } else if (Objects.equals(user.getRole(), "TEACHER")) {
            String teacherName = user.getLastName() + " " + user.getFirstName().charAt(0) + "." + user.getMiddleName().charAt(0) + ".";
            Log.d("MainActivity", "Fetching schedule for teacher: " + teacherName);
            Call<List<DayWeek>> call = apiService.getSchedulesByTeacherName(token, teacherName);
            call.enqueue(callback);
        }
    }


    /**
     * Метод для изменения подгруппы пользователя.
     * @param subgroupNumber номер новой подгруппы.
     */
    private void changeSubgroup(String subgroupNumber) {
        user.setSubgroup_number(subgroupNumber);
        Log.d("MainActivity", "Subgroup saved: " + subgroupNumber);
    }

    /**
     * Метод для переключения на первый экран.
     */
    private void switchToScreen1() {
        // Логика переключения на первый экран
        Intent intent = new Intent(this, TeachersActivity.class);
        startActivity(intent);
    }

    /**
     * Метод для переключения на второй экран.
     */
    private void switchToScreen2() {
        // Логика переключения на второй экран
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
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
            long currentTime = System.currentTimeMillis() / 1000 ;
            return exp > currentTime;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void logout() {
        // Очистить расписание в MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        // Получить доступ к SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Сохраняем логин и пароль
        boolean darkMode = sharedPreferences.getBoolean("dark_mode", false); // Сохраните текущее состояние темы
        String savedEmail = sharedPreferences.getString("saved_email", null);
        String savedPassword = sharedPreferences.getString("saved_password", null);

        // Очищаем все данные, кроме логина и пароля
        editor.clear();

        // Восстанавливаем логин и пароль
        if (savedEmail != null && savedPassword != null) {
            editor.putString("saved_email", savedEmail);
            editor.putString("saved_password", savedPassword);
            editor.putBoolean("remember_me", true);
            editor.putBoolean("dark_mode", darkMode); // Восстановите состояние темы
        }

        editor.apply();

        // Перейти к экрану входа
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.putExtra("from_main_activity_error", true);
        startActivity(loginIntent);
        finish();
    }

    /**
     * Метод для изменения четности недели.
     * @param newParity новое значение четности недели.
     */
    private void changeParity(String newParity) {
        // Сохранение нового значения parity в SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("parity", newParity);
        editor.apply();
        Log.d("MainActivity", "Parity saved: " + newParity);
    }
}
