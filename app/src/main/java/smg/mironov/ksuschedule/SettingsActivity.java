package smg.mironov.ksuschedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import smg.mironov.ksuschedule.Utils.SharedPrefManager;

public class SettingsActivity extends AppCompatActivity {

    private EditText groupEditText;
    private EditText subgroupEditText;
    private EditText weekEditText;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);

        // Инициализация элементов интерфейса
        groupEditText = findViewById(R.id.editTextGroup);
        subgroupEditText = findViewById(R.id.editTextSubgroup);
        weekEditText = findViewById(R.id.editTextWeek);

        ImageView editGroupIcon = findViewById(R.id.imageViewGroupEdit);
        ImageView editSubgroupIcon = findViewById(R.id.imageViewSubgroupEdit);
        ImageView editWeekIcon = findViewById(R.id.imageViewWeekEdit);

        // Настройка кнопок навигационной панели
        ImageView navButton1 = findViewById(R.id.teachers_icon);
        ImageView navButton2 = findViewById(R.id.home_icon);

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

        // Обработка нажатий на иконки редактирования
        editGroupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика смены группы
                changeGroup();
            }
        });

        editSubgroupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика смены подгруппы
                changeSubgroup();
            }
        });

        editWeekIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика смены недели
                changeWeek();
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void changeGroup() {
        String newGroup = groupEditText.getText().toString();
        // Сохранение новой группы (например, в SharedPreferences)
        sharedPrefManager.setGroupNumber(newGroup);
    }

    private void changeSubgroup() {
        String newSubgroup = subgroupEditText.getText().toString();
        sharedPrefManager.setSubgroupNumber(newSubgroup);

    }

    private void changeWeek() {
        String newWeek = weekEditText.getText().toString();
        // Сохранение новой недели (например, в SharedPreferences)
        sharedPrefManager.setParity(newWeek);
    }
}

