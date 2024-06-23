package smg.mironov.ksuschedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import smg.mironov.ksuschedule.Models.DayWeek;
import smg.mironov.ksuschedule.Models.Period;

public class DayWeekAdapter extends RecyclerView.Adapter<DayWeekAdapter.ScheduleViewHolder> {

    private List<DayWeek> scheduleList;
    private Context context;

    public DayWeekAdapter(Context context, List<DayWeek> scheduleList) {
        this.context = context;
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day_week, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        DayWeek dayWeek = scheduleList.get(position);

        holder.nameAndNumDay.setText(dayWeek.getDayWeek());

        // Привязка данных для первого периода
        holder.firstTimeStartAndEnd.setText(dayWeek.getTimeStart() + " " + dayWeek.getTimeEnd());
        holder.firstTypeAndNameDis.setText(dayWeek.getSubject().getType() + "|" + dayWeek.getSubject().getName());
        holder.firstInfoTeacher.setText(dayWeek.getTeacher().getName() + " " + dayWeek.getTeacher().getPost());
        holder.firstNumAudit.setText(dayWeek.getClassroom());

        // Очистка предыдущих представлений
        holder.additionalPeriodsLayout.removeAllViews();

        // Динамическое добавление дополнительных периодов
        for (Period period : dayWeek.getAdditionalPeriods()) {
            View periodView = LayoutInflater.from(context).inflate(R.layout.period_item, holder.additionalPeriodsLayout, false);

            TextView timeStartAndEnd = periodView.findViewById(R.id.timeStartAndEnd);
            TextView typeAndNameDis = periodView.findViewById(R.id.typeAndNameDis);
            TextView infoTeacher = periodView.findViewById(R.id.infoTeacher);
            TextView numAudit = periodView.findViewById(R.id.numAudit);

            timeStartAndEnd.setText(period.getTimeStart() + " " + period.getTimeEnd());
            typeAndNameDis.setText(period.getSubject().getType() + "|" + period.getSubject().getName());
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
