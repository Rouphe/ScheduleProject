package smg.mironov.ksuschedule.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import java.util.List;

import smg.mironov.ksuschedule.Models.TeacherDto;
import smg.mironov.ksuschedule.R;

public class TeacherAdapter extends ArrayAdapter<TeacherDto> {

    private OnItemClickListener clickListener;

    public TeacherAdapter(Context context, List<TeacherDto> teachers, OnItemClickListener listener) {
        super(context, 0, teachers);
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_info_teacher, parent, false);
        }

        final TeacherDto currentTeacher = getItem(position);

        TextView nameTextView = listItemView.findViewById(R.id.NameTeacher);
        nameTextView.setText(currentTeacher.getName());

        TextView positionTextView = listItemView.findViewById(R.id.PostTeacher);
        positionTextView.setText(currentTeacher.getPost());

        ImageView getInfo = listItemView.findViewById(R.id.blue_arrow_icon);

        // Обработка клика на элемент списка
        getInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick(currentTeacher.getId(), getInfo);
                }
            }
        });

        return listItemView;
    }

    // Интерфейс для обработки кликов на элементах списка
    public interface OnItemClickListener {
        void onItemClick(int teacherId, ImageView infoTeacher);
    }
}
