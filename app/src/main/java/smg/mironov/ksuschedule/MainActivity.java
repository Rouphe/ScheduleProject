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
import java.util.List;

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
        groupNumberTextView.setText(sharedPrefManager.getGroupNumber());
        subgroupSpinner = findViewById(R.id.Subgroup);
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

        String savedSubgroup = sharedPrefManager.getSubgroupNumber();
        if (savedSubgroup != null) {
            int position = spinnerAdapter.getPosition(savedSubgroup);
            subgroupSpinner.setSelection(position);
        }

        fetchSubgroups(sharedPrefManager.getSubgroupNumber());



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
                sharedPrefManager.setSubgroupNumber(selectedSubgroup); // Сохранение выбранной подгруппы
                fetchScheduleFromServer(); // Вызов метода для загрузки расписания по новой подгруппе
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Получение данных с сервера
        fetchScheduleFromServer();
    }

    private void fetchScheduleFromServer() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String savedSubgroupNumber = sharedPrefManager.getSubgroupNumber();
        Call<List<DayWeek>> call = apiService.getSchedulesBySubgroupNumber(savedSubgroupNumber);
        call.enqueue(new Callback<List<DayWeek>>() {
            @Override
            public void onResponse(Call<List<DayWeek>> call, Response<List<DayWeek>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DayWeek> scheduleList = response.body();
                    scheduleAdapter.updateScheduleList(scheduleList);
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
        });
    }

    private void fetchSubgroups(String groupNumber) {
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
                }
            }

            @Override
            public void onFailure(Call<List<SubgroupDto>> call, Throwable t) {
                // Handle errors
            }
        });
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
