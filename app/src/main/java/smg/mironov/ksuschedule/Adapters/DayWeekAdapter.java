package smg.mironov.ksuschedule.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import smg.mironov.ksuschedule.Models.DayWeek;
import smg.mironov.ksuschedule.Models.GroupDto;
import smg.mironov.ksuschedule.R;
import smg.mironov.ksuschedule.Utils.SharedPrefManager;

public class DayWeekAdapter extends RecyclerView.Adapter<DayWeekAdapter.ScheduleViewHolder> {
    private Context context;
    private List<String> dayList;
    private Map<String, List<DayWeek>> scheduleMap;
    private SharedPreferences sharedPreferences;
    private String filterParity;

    public DayWeekAdapter(Context context) {
        this.context = context;
        this.dayList = new ArrayList<>();
        this.scheduleMap = new HashMap<>();
        this.sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        this.filterParity = sharedPreferences.getString("user_parity", "");
    }

    public void setFilterParity(String filterParity) {
        this.filterParity = filterParity;
    }

    public void updateScheduleList(List<DayWeek> newScheduleList) {
        scheduleMap.clear();
        dayList.clear();

        // Фильтрация по parity
        for (DayWeek dayWeek : newScheduleList) {
            if (filterParity == null || filterParity.equals(dayWeek.getParity()) || "ВСЕГДА".equals(dayWeek.getParity())) {
                String day = dayWeek.getDayWeek();
                if (!scheduleMap.containsKey(day)) {
                    scheduleMap.put(day, new ArrayList<>());
                    dayList.add(day);
                }
                scheduleMap.get(day).add(dayWeek);
            }
        }

        // Сортировка по дням недели
        Collections.sort(dayList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(getDayOfWeekOrder(o1), getDayOfWeekOrder(o2));
            }
        });

        // Сортировка пар внутри каждого дня по времени начала
        for (String day : scheduleMap.keySet()) {
            Collections.sort(scheduleMap.get(day), new Comparator<DayWeek>() {
                @Override
                public int compare(DayWeek o1, DayWeek o2) {
                    return LocalTime.parse(o1.getTimeStart()).compareTo(LocalTime.parse(o2.getTimeStart()));
                }
            });
        }

        notifyDataSetChanged();
    }

    private int getDayOfWeekOrder(String dayOfWeek) {
        switch (dayOfWeek) {
            case "ПОНЕДЕЛЬНИК": return 1;
            case "ВТОРНИК": return 2;
            case "СРЕДА": return 3;
            case "ЧЕТВЕРГ": return 4;
            case "ПЯТНИЦА": return 5;
            case "СУББОТА": return 6;
            case "ВОСКРЕСЕНЬЕ": return 7;
            default: return 8; // В случае неизвестного дня недели
        }
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day_week, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        String day = dayList.get(position);
        List<DayWeek> daySchedule = scheduleMap.get(day);

        // Получаем текущую дату
        LocalDate today = LocalDate.now();

        // Получаем день недели для текущей даты
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();

        // Вычисляем дату начала текущей недели (понедельник)
        LocalDate startOfWeek = today.minusDays(currentDayOfWeek.getValue() - DayOfWeek.MONDAY.getValue());

        // Определяем текущую неделю
        boolean isEvenWeek = startOfWeek.get(WeekFields.ISO.weekOfWeekBasedYear()) % 2 == 0;
        String currentWeekParity = isEvenWeek ? "ЧИСЛИТЕЛЬ" : "ЗНАМЕНАТЕЛЬ";

        this.filterParity = isEvenWeek ? "ЧИСЛИТЕЛЬ" : "ЗНАМЕНАТЕЛЬ";

        // Save parity to SharedPreferences
        sharedPreferences.edit().putString("user_parity", filterParity).apply();


        // Если parity в SharedPrefManager не совпадает с текущей неделей, показываем расписание следующей недели
        String savedParity = sharedPreferences.getString("parity", "");
        if (!currentWeekParity.equals(savedParity)) {
            startOfWeek = startOfWeek.plusWeeks(1);
            isEvenWeek = !isEvenWeek;
            currentWeekParity = isEvenWeek ? "ЧИСЛИТЕЛЬ" : "ЗНАМЕНАТЕЛЬ";
            sharedPreferences.edit().putString("parity", currentWeekParity).apply();
        }

        // Добавляем количество дней, соответствующее позиции в RecyclerView
        LocalDate dateToShow = startOfWeek.plusDays(getDayOfWeekOrder(day) - 1);

        // Форматируем дату в требуемый формат
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");
        String formattedDate = dateToShow.format(formatter);

        holder.nameDay.setText(day);
        holder.numDay.setText(formattedDate);

        holder.pairsLayout.removeAllViews();

        String timeStartAndEndPrevious = "";

        for (DayWeek dayWeek : daySchedule) {
            View pairView = LayoutInflater.from(context).inflate(R.layout.period_item, holder.pairsLayout, false);
            TextView timeStartAndEnd = pairView.findViewById(R.id.FirstTimeStartAndEnd);
            TextView typeAndNameDis = pairView.findViewById(R.id.typeAndNameDis);
            TextView infoTeacher = pairView.findViewById(R.id.infoTeacher);

            String role = sharedPreferences.getString("user_role", "");

            if (Objects.equals(role, "TEACHER")) {
                infoTeacher.setText(dayWeek.getSubgroup().getNumber());
            } else {
                infoTeacher.setText(dayWeek.getTeacher().getName() + " " + dayWeek.getTeacher().getPost());
            }


            TextView numAudit = pairView.findViewById(R.id.numAudit);

            timeStartAndEnd.setText(dayWeek.getTimeStart() + " " + dayWeek.getTimeEnd());
            typeAndNameDis.setText(dayWeek.getSubject().getType() + " | " + dayWeek.getSubject().getName());

            numAudit.setText(dayWeek.getClassroom());

            if (timeStartAndEndPrevious.equals(dayWeek.getTimeStart() + " " + dayWeek.getTimeEnd())){
                continue;
            }

            timeStartAndEndPrevious = dayWeek.getTimeStart() + " " + dayWeek.getTimeEnd();

            // Проверка, идет ли текущая пара
            LocalTime timeStart = LocalTime.parse(dayWeek.getTimeStart(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime timeEnd = LocalTime.parse(dayWeek.getTimeEnd(), DateTimeFormatter.ofPattern("HH:mm"));

            LocalDate localDate = LocalDate.now();
            String date1 = localDate.format(formatter);

            LocalTime now = LocalTime.now();
            if (now.isAfter(timeStart) && now.isBefore(timeEnd) && (formattedDate.equals(date1))) {
                pairView.setBackgroundResource(R.drawable.custom_pair_is_now); // Выделяем цветом текущую пару
            } else {
                pairView.setBackgroundColor(Color.TRANSPARENT); // Убираем выделение, если не текущая пара
            }

            holder.pairsLayout.addView(pairView);
        }
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView nameDay;
        TextView numDay;
        LinearLayout pairsLayout;

        ScheduleViewHolder(View itemView) {
            super(itemView);
            nameDay = itemView.findViewById(R.id.NameDay);
            numDay = itemView.findViewById(R.id.NumDay);
            pairsLayout = itemView.findViewById(R.id.additionalPeriodsLayout);
        }
    }
}
