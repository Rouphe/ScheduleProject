package smg.mironov.ksuschedule;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText groupEditText;
    private EditText subgroupEditText;
    private EditText weekEditText;

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
        LinearLayout navButton1 = findViewById(R.id.settings_button);
        LinearLayout navButton2 = findViewById(R.id.home_button);

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
    }

    private void switchToScreen2() {
        // Логика переключения на второй экран
    }

    private void changeGroup() {
        String newGroup = groupEditText.getText().toString();
        // Сохранение новой группы (например, в SharedPreferences)
    }

    private void changeSubgroup() {
        String newSubgroup = subgroupEditText.getText().toString();
        // Сохранение новой подгруппы (например, в SharedPreferences)
    }

    private void changeWeek() {
        String newWeek = weekEditText.getText().toString();
        // Сохранение новой недели (например, в SharedPreferences)
    }
}

