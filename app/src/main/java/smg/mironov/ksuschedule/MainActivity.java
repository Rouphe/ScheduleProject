package smg.mironov.ksuschedule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DayWeekAdapter scheduleAdapter;
    private String subgroup_number = "109.1";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        // Инициализация кнопок навигационной панели
        ImageView navButton1 = findViewById(R.id.teachers_icon);
        ImageView navButton2 = findViewById(R.id.settings_icon);

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


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация адаптера с пустым списком
        scheduleAdapter = new DayWeekAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(scheduleAdapter);

        // Получение данных с сервера
        fetchScheduleFromServer();
    }

    private void fetchScheduleFromServer() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<DayWeek>> call = apiService.getSchedulesBySubgroupNumber("109.1");
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
