package smg.mironov.ksuschedule.Utils;

import java.util.ArrayList;
import java.util.List;

import smg.mironov.ksuschedule.Models.DayWeek;

public class DaySchedule {
    private String dayWeek;
    private List<DayWeek> dayWeeks;

    public DaySchedule(String dayWeek) {
        this.dayWeek = dayWeek;
        this.dayWeeks = new ArrayList<>();
    }

    public String getDayWeek() {
        return dayWeek;
    }

    public List<DayWeek> getDayWeeks() {
        return dayWeeks;
    }

    public void addDayWeek(DayWeek dayWeek) {
        this.dayWeeks.add(dayWeek);
    }
}
