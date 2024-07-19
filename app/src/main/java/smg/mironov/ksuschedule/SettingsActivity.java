package smg.mironov.ksuschedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

        Spinner editTextWeek = findViewById(R.id.editTextWeek);
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"ЧИСЛИТЕЛЬ", "ЗНАМЕНАТЕЛЬ"});
        editTextWeek.setAdapter(spinnerAdapter);

        // Устанавливаем значение Spinner из SharedPreferences
        String savedParity = sharedPreferences.getString("parity", null);
        if (savedParity != null) {
            int spinnerPosition = spinnerAdapter.getPosition(savedParity);
            editTextWeek.setSelection(spinnerPosition);
        }

        editTextWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = (String) parent.getItemAtPosition(position);
                changeParity(selectedValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
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

    /**
     * Метод для изменения четности недели.
     * @param newParity новое значение четности недели.
     */
    private void changeParity(String newParity) {
        // Сохранение нового значения parity в SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("parity", newParity);
        editor.apply();
        Log.d("SettingsActivity", "Parity saved: " + newParity);
    }
}
