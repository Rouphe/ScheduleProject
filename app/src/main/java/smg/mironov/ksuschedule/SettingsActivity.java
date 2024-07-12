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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {

    private EditText groupEditText;
    private SharedPreferences sharedPreferences;
    private TextView saveButton;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Инициализация элементов интерфейса
        groupEditText = findViewById(R.id.editTextGroup);
        saveButton = findViewById(R.id.SaveAll);

        ImageView editGroupIcon = findViewById(R.id.imageViewGroupEdit);


        groupEditText.setText(sharedPreferences.getString("user_groupNumber", ""));

        Spinner editTextWeek = findViewById(R.id.editTextWeek);
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"ЧИСЛИТЕЛЬ", "ЗНАМЕНАТЕЛЬ"});
        editTextWeek.setAdapter(spinnerAdapter);

        // Устанавливаем значение Spinner из SharedPreferences
        String savedParity = sharedPreferences.getString("user_parity", null);
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

    private void back() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void changeGroup() {
        String newGroup = groupEditText.getText().toString();
        // Сохранение новой группы в SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_groupNumber", newGroup);
        editor.apply();
    }

    private void changeParity(String newParity) {
        // Сохранение нового значения parity в SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_parity", newParity);
        editor.apply();
        Log.d("SettingsActivity", "Parity saved: " + newParity);
    }
}
