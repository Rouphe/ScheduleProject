package smg.mironov.ksuschedule;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import smg.mironov.ksuschedule.Models.DayWeek;
import smg.mironov.ksuschedule.Models.Period;
import smg.mironov.ksuschedule.Models.Subject;
import smg.mironov.ksuschedule.Models.Teacher;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private smg.mironov.ksuschedule.DayWeekAdapter scheduleAdapter;
    private List<DayWeek> dayWeekList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Пример данных для тестирования
        dayWeekList = new ArrayList<>();

        // Добавление данных в dayWeekList...
        dayWeekList.add(createSampleDayWeek());

        scheduleAdapter = new smg.mironov.ksuschedule.DayWeekAdapter(this, dayWeekList);
        recyclerView.setAdapter(scheduleAdapter);
    }

    private DayWeek createSampleDayWeek() {
        DayWeek dayWeek = new DayWeek();
        dayWeek.setDayWeek("Понедельник");
        dayWeek.setTimeStart("09:00");
        dayWeek.setTimeEnd("10:30");
        dayWeek.setSubject(new Subject("Лекция", "Математика"));
        dayWeek.setTeacher(new Teacher("Иванов И.И.", "Профессор"));
        dayWeek.setClassroom("101");

        List<Period> additionalPeriods = new ArrayList<>();
        additionalPeriods.add(new Period("10:45", "12:15", new Subject("Практика", "Физика"), new Teacher("Петров П.П.", "Доцент"), "102"));
        additionalPeriods.add(new Period("12:30", "14:00", new Subject("Лекция", "Химия"), new Teacher("Сидоров С.С.", "Ассистент"), "103"));
        dayWeek.setAdditionalPeriods(additionalPeriods);

        return dayWeek;
    }
}
