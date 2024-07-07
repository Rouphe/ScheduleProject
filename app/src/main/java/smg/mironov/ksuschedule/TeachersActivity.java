package smg.mironov.ksuschedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Adapters.TeacherAdapter;
import smg.mironov.ksuschedule.Models.TeacherDto;

public class TeachersActivity extends AppCompatActivity {

    private ListView listView;
    private TeacherAdapter adapter;
    private List<TeacherDto> teacherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_teacher_screen);

        // Инициализация ListView и адаптера
        listView = findViewById(R.id.list_teachers);
        teacherList = new ArrayList<>();

        adapter = new TeacherAdapter(this, teacherList);
        listView.setAdapter(adapter);

        // Инициализация кнопок навигационной панели
        ImageView navButton1 = findViewById(R.id.home_icon);
        ImageView navButton2 = findViewById(R.id.profile_icon);

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

        // Получение данных с сервера
        fetchTeachersFromServer();
    }

    private void fetchTeachersFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.15:8081/") // Замените на ваш базовый URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<TeacherDto>> call = apiService.getAllTeachers();
        call.enqueue(new Callback<List<TeacherDto>>() {
            @Override
            public void onResponse(Call<List<TeacherDto>> call, Response<List<TeacherDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    teacherList.clear();
                    teacherList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    // Логика обработки кода ошибки
                    Toast.makeText(TeachersActivity.this, "Ошибка при получении данных: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TeacherDto>> call, Throwable t) {
                // Логика обработки ошибки соединения
                Toast.makeText(TeachersActivity.this, "Ошибка соединения: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void switchToScreen1() {
        // Логика переключения на первый экран
        // Например, запуск новой активности:
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void switchToScreen2() {
        // Логика переключения на второй экран
        // Например, запуск новой активности:
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
