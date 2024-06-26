package smg.mironov.ksuschedule;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Models.SubgroupDto;

import smg.mironov.ksuschedule.Utils.SharedPrefManager;

public class FirstActivity extends AppCompatActivity {

    private EditText groupNumberEditText;
    private Spinner subgroupSpinner;
    private ArrayAdapter<String> spinnerAdapter;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;
    private LinearLayout LoginButton;
    private Spinner studOrTeachSpinner;
    private ArrayAdapter<String> getSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen);

        // Инициализация SharedPrefManager
        sharedPrefManager = SharedPrefManager.getInstance(this);

        if (!sharedPrefManager.isFirstTimeUser()) {
            switchToMain();
        }
        else {
            sharedPrefManager.setFirstTimeUser(false);
        }


        groupNumberEditText = findViewById(R.id.GroupNumber);
        subgroupSpinner = findViewById(R.id.SubgroupNumber);
        LoginButton = findViewById(R.id.login);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subgroupSpinner.setAdapter(spinnerAdapter);

        Spinner spinner = findViewById(R.id.Role);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Преподаватель", "Студент"});
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = (String) parent.getItemAtPosition(position);
                if (Objects.equals(selectedValue, "Преподаватель")){
                    sharedPrefManager.setRole(selectedValue);
                    subgroupSpinner.setVisibility(View.INVISIBLE);
                    groupNumberEditText.setHint("Введите ФИО...");
                }
                else {
                    sharedPrefManager.setRole(selectedValue);
                    subgroupSpinner.setVisibility(View.VISIBLE);
                    groupNumberEditText.setHint("Введите номер группы...");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        apiService = ApiClient.getClient().create(ApiService.class);

        // Восстановление сохраненного значения
        groupNumberEditText.setText(sharedPrefManager.getGroupNumber());

        groupNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchSubgroups(s.toString());
                if (Objects.equals(sharedPrefManager.getRole(), "Преподаватель")){
                    sharedPrefManager.setTeacherName(s.toString());
                }
                else {
                    fetchSubgroups(s.toString());
                    sharedPrefManager.setGroupNumber(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        String savedGroupNumber = sharedPrefManager.getGroupNumber();
        if (!savedGroupNumber.isEmpty()) {
            fetchSubgroups(savedGroupNumber);
        }

        subgroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSubgroup = (String) parent.getItemAtPosition(position);
                sharedPrefManager.setSubgroupNumber(selectedSubgroup); // Сохранение выбранной подгруппы
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMain();
            }
        });
    }

    private void fetchSubgroups(String groupNumber) {
        Call<List<SubgroupDto>> call = apiService.getSubgroupsByGroupNumber(groupNumber);
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

    private void switchToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Завершение текущей активности, чтобы не возвращаться сюда по кнопке "назад"
    }
}
