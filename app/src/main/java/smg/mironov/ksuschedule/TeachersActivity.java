package smg.mironov.ksuschedule;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import smg.mironov.ksuschedule.Adapters.TeacherAdapter;

public class TeachersActivity extends AppCompatActivity {

    private ListView listView;
    private TeacherAdapter adapter;
    private List<Teacher> teacherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_teacher_screen);

        // Инициализация ListView и адаптера
        listView = findViewById(R.id.list_teachers);
        teacherList = new ArrayList<>();

        // Получение данных с сервера (упрощенно)
        fetchTeachersFromServer();

        adapter = new TeacherAdapter(this, teacherList);
        listView.setAdapter(adapter);

        // Инициализация кнопок навигационной панели
        LinearLayout navButton1 = findViewById(R.id.home_button);
        LinearLayout navButton2 = findViewById(R.id.settings_button);

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
    }

    private void fetchTeachersFromServer() {
        // Здесь должна быть логика получения данных с сервера.
        // Пример данных:
        teacherList.add(new Teacher("Иван Иванов", "Профессор"));
        teacherList.add(new Teacher("Петр Петров", "Доцент"));
        // ...
    }

    private void switchToScreen1() {
        // Логика переключения на первый экран
    }

    private void switchToScreen2() {
        // Логика переключения на второй экран
    }
}

