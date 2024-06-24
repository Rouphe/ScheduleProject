package smg.mironov.ksuschedule.Adapters;

import android.content.Context;
import android.icu.util.LocaleData;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import smg.mironov.ksuschedule.Models.DayWeek;
import smg.mironov.ksuschedule.Models.Period;
import smg.mironov.ksuschedule.R;

public class DayWeekAdapter extends RecyclerView.Adapter<DayWeekAdapter.ScheduleViewHolder> {
    private Context context;
    private List<DayWeek> scheduleList;

    //TODO: три глобальные переменные: тип недели, номер группы, номер подгруппы

    public DayWeekAdapter(Context context, List<DayWeek> scheduleList) {
        this.context = context;
        this.scheduleList = scheduleList;
    }

    public void updateScheduleList(List<DayWeek> newScheduleList) {
        this.scheduleList = newScheduleList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day_week, parent, false);
        return new ScheduleViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        DayWeek dayWeek = scheduleList.get(position);

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

        holder.nameAndNumDay.setText(dayWeek.getDayWeek() + " | " + formattedDate);
        holder.firstTimeStartAndEnd.setText(dayWeek.getTimeStart() + " " + dayWeek.getTimeEnd());
        //TODO: Проблема десереализации (не получает данные для предметов и преподов) вроде решена??
        holder.firstTypeAndNameDis.setText(dayWeek.getSubject().getType() + " | " + dayWeek.getSubject().getName());
        holder.firstInfoTeacher.setText(dayWeek.getTeacher().getName() + " " + dayWeek.getTeacher().getPost());
        holder.firstNumAudit.setText(dayWeek.getClassroom());

        holder.additionalPeriodsLayout.removeAllViews();

        for (Period period : dayWeek.getAdditionalPeriods()) {
            View periodView = LayoutInflater.from(context).inflate(R.layout.period_item, holder.additionalPeriodsLayout, false);
            TextView timeStartAndEnd = periodView.findViewById(R.id.timeStartAndEnd);
            TextView typeAndNameDis = periodView.findViewById(R.id.typeAndNameDis);
            TextView infoTeacher = periodView.findViewById(R.id.infoTeacher);
            TextView numAudit = periodView.findViewById(R.id.numAudit);

            timeStartAndEnd.setText(period.getTimeStart() + " " + period.getTimeEnd());
            typeAndNameDis.setText(period.getSubject().getType() + " | " + period.getSubject().getName());
            infoTeacher.setText(period.getTeacher().getName() + " " + period.getTeacher().getPost());
            numAudit.setText(period.getClassroom());

            holder.additionalPeriodsLayout.addView(periodView);
        }

    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView nameAndNumDay;
        TextView firstTimeStartAndEnd;
        TextView firstTypeAndNameDis;
        TextView firstInfoTeacher;
        TextView firstNumAudit;
        LinearLayout additionalPeriodsLayout;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameAndNumDay = itemView.findViewById(R.id.NameAndNumDay);
            firstTimeStartAndEnd = itemView.findViewById(R.id.FirstTimeStartAndEnd);
            firstTypeAndNameDis = itemView.findViewById(R.id.FirstTypeAndNameDis);
            firstInfoTeacher = itemView.findViewById(R.id.FirstInfoTeacher);
            firstNumAudit = itemView.findViewById(R.id.FirstNumAudit);
            additionalPeriodsLayout = itemView.findViewById(R.id.additionalPeriodsLayout);
        }
    }
}
