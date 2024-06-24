package smg.mironov.ksuschedule.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import java.util.Map;


import smg.mironov.ksuschedule.Models.DayWeek;

import smg.mironov.ksuschedule.R;


import smg.mironov.ksuschedule.Utils.DaySchedule;
import smg.mironov.ksuschedule.Utils.SharedPrefManager;


public class DayWeekAdapter extends RecyclerView.Adapter<DayWeekAdapter.ScheduleViewHolder> {
    private Context context;
    private List<DaySchedule> scheduleList;
    private SharedPrefManager sharedPrefManager;
    private String filterParity;

    public DayWeekAdapter(Context context) {
        this.context = context;
        this.scheduleList = new ArrayList<>();
        this.sharedPrefManager = new SharedPrefManager(context); // Инициализация SharedPrefManager
        this.filterParity = sharedPrefManager.getParity();
    }

    public void setFilterParity(String filterParity) {
        this.filterParity = filterParity;
    }

    public void updateScheduleList(List<DayWeek> newScheduleList) {
        Map<String, DaySchedule> dayScheduleMap = new HashMap<>();
        Map<String, Integer> dayOfWeekOrder = new HashMap<>();

        // Определяем порядок дней недели
        dayOfWeekOrder.put("ПОНЕДЕЛЬНИК", 1);
        dayOfWeekOrder.put("ВТОРНИК", 2);
        dayOfWeekOrder.put("СРЕДА", 3);
        dayOfWeekOrder.put("ЧЕТВЕРГ", 4);
        dayOfWeekOrder.put("ПЯТНИЦА", 5);
        dayOfWeekOrder.put("СУББОТА", 6);
        dayOfWeekOrder.put("ВОСКРЕСЕНЬЕ", 7);

        for (DayWeek dayWeek : newScheduleList) {
            if (filterParity != null && !filterParity.equals(dayWeek.getParity()) && !"ВСЕГДА".equals(dayWeek.getParity())) {
                continue; // Фильтруем по parity
            }

            String day = dayWeek.getDayWeek();
            if (!dayScheduleMap.containsKey(day)) {
                dayScheduleMap.put(day, new DaySchedule(day));
            }
            dayScheduleMap.get(day).addDayWeek(dayWeek);
        }

        List<DaySchedule> sortedScheduleList = new ArrayList<>(dayScheduleMap.values());
        Collections.sort(sortedScheduleList, new Comparator<DaySchedule>() {
            @Override
            public int compare(DaySchedule o1, DaySchedule o2) {
                return Integer.compare(dayOfWeekOrder.get(o1.getDayWeek()), dayOfWeekOrder.get(o2.getDayWeek()));
            }
        });

        this.scheduleList.clear();
        this.scheduleList.addAll(sortedScheduleList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day_week, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        DaySchedule daySchedule = scheduleList.get(position);

        // Получаем текущую дату
        LocalDate today = LocalDate.now();

        // Получаем день недели для текущей даты
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();

        // Вычисляем дату начала текущей недели (понедельник)
        LocalDate startOfWeek = today.minusDays(currentDayOfWeek.getValue() - DayOfWeek.MONDAY.getValue());

        // Добавляем количество дней, соответствующее позиции в RecyclerView
        LocalDate dateToShow = startOfWeek.plusDays(position);

        // Форматируем дату в требуемый формат
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");
        String formattedDate = dateToShow.format(formatter);

        holder.nameDay.setText(daySchedule.getDayWeek());
        holder.numDay.setText(formattedDate);

        holder.pairsLayout.removeAllViews();

        for (DayWeek dayWeek : daySchedule.getDayWeeks()) {
            View pairView = LayoutInflater.from(context).inflate(R.layout.period_item, holder.pairsLayout, false);
            TextView timeStartAndEnd = pairView.findViewById(R.id.FirstTimeStartAndEnd);
            TextView typeAndNameDis = pairView.findViewById(R.id.typeAndNameDis);
            TextView infoTeacher = pairView.findViewById(R.id.infoTeacher);
            TextView numAudit = pairView.findViewById(R.id.numAudit);

            timeStartAndEnd.setText(dayWeek.getTimeStart() + " " + dayWeek.getTimeEnd());
            typeAndNameDis.setText(dayWeek.getSubject().getType() + " | " + dayWeek.getSubject().getName());
            infoTeacher.setText(dayWeek.getTeacher().getName() + " " + dayWeek.getTeacher().getPost());
            numAudit.setText(dayWeek.getClassroom());

            holder.pairsLayout.addView(pairView);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView nameDay;
        TextView numDay;
        LinearLayout pairsLayout;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameDay = itemView.findViewById(R.id.NameDay);
            numDay = itemView.findViewById(R.id.NumDay);
            pairsLayout = itemView.findViewById(R.id.additionalPeriodsLayout);
        }
    }
}
