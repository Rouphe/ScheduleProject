package smg.mironov.ksuschedule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import smg.mironov.ksuschedule.Utils.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DayWeekAdapter scheduleAdapter;
    private SharedPrefManager sharedPrefManager;
    private Spinner subgroupSpinner;
    private ApiService apiService;
    private ArrayAdapter<String> spinnerAdapter;
    private TextView parity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        sharedPrefManager = new SharedPrefManager(this);
        TextView groupNumberTextView = findViewById(R.id.group_number);
        if (Objects.equals(sharedPrefManager.getRole(), "Преподаватель")){
            groupNumberTextView.setVisibility(View.INVISIBLE);
            TextView teacherName = findViewById(R.id.Group);
            teacherName.setText(sharedPrefManager.getTeacherName());

        }

        groupNumberTextView.setText(sharedPrefManager.getGroupNumber());
        subgroupSpinner = findViewById(R.id.Subgroup);
        if (sharedPrefManager.getRole().equals("Преподаватель")){
            subgroupSpinner.setVisibility(View.INVISIBLE);
        }
        parity = findViewById(R.id.weekType);

        parity.setText(sharedPrefManager.getParity());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация адаптера
        scheduleAdapter = new DayWeekAdapter(this);
        recyclerView.setAdapter(scheduleAdapter);


        // Пример данных
        List<DayWeek> scheduleList = new ArrayList<>();

        // Инициализация кнопок навигационной панели
        ImageView navButton1 = findViewById(R.id.teachers_icon);
        ImageView navButton2 = findViewById(R.id.settings_icon);

        subgroupSpinner = findViewById(R.id.Subgroup);
        spinnerAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subgroupSpinner.setAdapter(spinnerAdapter);


        apiService = ApiClient.getClient().create(ApiService.class);

        fetchSubgroups();



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
                changeSubgroup(selectedSubgroup); // Сохранение выбранной подгруппы
                 // Вызов метода для загрузки расписания по новой подгруппе
                fetchScheduleFromServer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Получение данных с сервера
        //fetchScheduleFromServer();
    }
    private void fetchSubgroups() {
        String savedGroupNumber = sharedPrefManager.getGroupNumber();
        Call<List<SubgroupDto>> call = apiService.getSubgroupsByGroupNumber(savedGroupNumber);
        call.enqueue(new Callback<List<SubgroupDto>>() {
            @Override
            public void onResponse(Call<List<SubgroupDto>> call, Response<List<SubgroupDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SubgroupDto> subgroups = response.body();
                    List<String> subgroupNumbers = new ArrayList<>();
                    for (SubgroupDto subgroup : subgroups) {
                        subgroupNumbers.add(subgroup.getNumber());
                    }
                    spinnerAdapter.clear();
                    spinnerAdapter.addAll(subgroupNumbers);
                    spinnerAdapter.notifyDataSetChanged();

                    // Установить сохраненное значение подгруппы после загрузки данных с сервера
                    String savedSubgroup = sharedPrefManager.getSubgroupNumber();
                    if (savedSubgroup != null) {
                        int subgroupPosition = spinnerAdapter.getPosition(savedSubgroup);
                        if (subgroupPosition >= 0) {
                            subgroupSpinner.setSelection(subgroupPosition);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SubgroupDto>> call, Throwable t) {
                // Handle errors
            }
        });
    }
    private void fetchScheduleFromServer() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String savedSubgroupNumber = sharedPrefManager.getSubgroupNumber();
        Callback<List<DayWeek>> callback = new Callback<List<DayWeek>>() {
            @Override
            public void onResponse(Call<List<DayWeek>> call, Response<List<DayWeek>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DayWeek> scheduleList = response.body();
                    // Используем Set для удаления дубликатов
                    Set<DayWeek> uniqueScheduleSet = new HashSet<>(scheduleList);
                    List<DayWeek> uniqueScheduleList = new ArrayList<>(uniqueScheduleSet);

                    scheduleAdapter.updateScheduleList(uniqueScheduleList);
                } else {
                    Log.e("MainActivity", "Response not successful");
                    Toast.makeText(MainActivity.this, "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DayWeek>> call, Throwable t) {
                Log.e("MainActivity", "Error fetching schedule", t);
                Toast.makeText(MainActivity.this, "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
            }
        };

        if (Objects.equals(sharedPrefManager.getRole(), "Студент")) {
            Call<List<DayWeek>> call = apiService.getSchedulesBySubgroupNumber(savedSubgroupNumber);
            call.enqueue(callback);
        } else {
            String savedTeacherName = sharedPrefManager.getTeacherName();
            Call<List<DayWeek>> call = apiService.getSchedulesByTeacherName(savedTeacherName);
            call.enqueue(callback);
        }
    }





    private void changeSubgroup(String subgroupNumber) {
        sharedPrefManager.setSubgroupNumber(subgroupNumber);
        Log.d("MainActivity", "Subgroup saved: " + subgroupNumber);
    }

    private void switchToScreen1() {
        // Логика переключения на первый экран
        // Например, запуск новой активности:
        Intent intent = new Intent(this, TeachersActivity.class);
        startActivity(intent);
    }

    private void switchToScreen2() {
        // Логика переключения на второй экран
        // Например, запуск новой активности:
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
