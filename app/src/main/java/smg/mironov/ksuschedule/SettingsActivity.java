package smg.mironov.ksuschedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Класс {@link SettingsActivity} отвечает за экран настроек приложения, позволяющий пользователю изменить группу и четность недели.
 *
 * @author Егор Гришанов, Алекснадр Миронов
 *
 * @version 1.0
 */
public class SettingsActivity extends AppCompatActivity {

    /** Поле для ввода номера группы */
    private EditText groupEditText;
    /** Объект для работы с SharedPreferences */
    private SharedPreferences sharedPreferences;
    /** Кнопка для сохранения изменений */
    private TextView saveButton;
    /** Адаптер для Spinner */
    private ArrayAdapter<String> spinnerAdapter;
    /** Контейнер для редактирования группы */
    private LinearLayout groupEditContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        groupEditContainer = findViewById(R.id.editGroupContainer);

        if (sharedPreferences.getString("user_role", null).equals("TEACHER")) {
            groupEditContainer.setVisibility(View.GONE);
        }

        // Инициализация элементов интерфейса
        groupEditText = findViewById(R.id.editTextGroup);
        saveButton = findViewById(R.id.SaveAll);

        ImageView editGroupIcon = findViewById(R.id.imageViewGroupEdit);

        groupEditText.setText(sharedPreferences.getString("user_groupNumber", ""));



        Switch themeSwitch = findViewById(R.id.themeSwitch);

        // Получаем SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Установка начального состояния переключателя
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        themeSwitch.setChecked(currentNightMode == Configuration.UI_MODE_NIGHT_YES);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editor.putBoolean("dark_mode", true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                editor.putBoolean("dark_mode", false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            editor.apply();

            // Перезапуск активности для применения темы
            recreate();
        });






        // Обработка нажатий на иконки редактирования
        editGroupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeGroup();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeGroup();
                back();
            }
        });
    }



    /**
     * Метод для перехода назад на экран профиля.
     */
    private void back() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Метод для изменения номера группы.
     */
    private void changeGroup() {
        String newGroup = groupEditText.getText().toString();
        // Сохранение новой группы в SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_groupNumber", newGroup);
        editor.apply();
    }


}
