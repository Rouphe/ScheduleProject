package smg.mironov.ksuschedule.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;


import java.util.List;

import smg.mironov.ksuschedule.Models.TeacherDto;
import smg.mironov.ksuschedule.R;

public class TeacherAdapter extends ArrayAdapter<TeacherDto> {

    public TeacherAdapter(Context context, List<TeacherDto> teachers) {
        super(context, 0, teachers);
    }

    @NonNull
    @Override
    public View getView(int post, @NonNull View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_info_teacher, parent, false);
        }

        TeacherDto currentTeacher = getItem(post);

        TextView nameTextView = listItemView.findViewById(R.id.NameTeacher);
        nameTextView.setText(currentTeacher.getName());

        TextView positionTextView = listItemView.findViewById(R.id.PostTeacher);
        positionTextView.setText(currentTeacher.getPost());


        return listItemView;
    }
}

