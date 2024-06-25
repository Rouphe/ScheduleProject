package smg.mironov.ksuschedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

import smg.mironov.ksuschedule.Utils.SharedPrefManager;

public class SettingsActivity extends AppCompatActivity {

    private EditText groupEditText;
    private EditText subgroupEditText;
    private SharedPrefManager sharedPrefManager;
    private TextView saveButton;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
        sharedPrefManager = new SharedPrefManager(this);

        // Инициализация элементов интерфейса
        groupEditText = findViewById(R.id.editTextGroup);
        saveButton = findViewById(R.id.SaveAll);

        ImageView editGroupIcon = findViewById(R.id.imageViewGroupEdit);

        // Настройка кнопок навигационной панели
        ImageView navButton1 = findViewById(R.id.teachers_icon);
        ImageView navButton2 = findViewById(R.id.home_icon);

        groupEditText.setText(sharedPrefManager.getGroupNumber());





        Spinner editTextWeek = findViewById(R.id.editTextWeek);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"ЧИСЛИТЕЛЬ", "ЗНАМЕНАТЕЛЬ"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) ;
        editTextWeek.setAdapter(spinnerAdapter);

        // Устанавливаем значение Spinner из SharedPreferences
        String savedParity = sharedPrefManager.getParity();
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeGroup();
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

    private void changeParity(String newParity) {
        sharedPrefManager.setParity(newParity);
        Log.d("SettingsActivity", "Parity saved: " + newParity);
    }



}

