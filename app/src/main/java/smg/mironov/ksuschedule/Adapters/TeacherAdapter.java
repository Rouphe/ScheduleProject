package smg.mironov.ksuschedule.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;


import java.util.List;

import smg.mironov.ksuschedule.R;
import smg.mironov.ksuschedule.Models.Teacher;

public class TeacherAdapter extends ArrayAdapter<Teacher> {

    public TeacherAdapter(Context context, List<Teacher> teachers) {
        super(context, 0, teachers);
    }

    @NonNull
    @Override
    public View getView(int post, @NonNull View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_info_teacher, parent, false);
        }

        Teacher currentTeacher = getItem(post);

        TextView nameTextView = listItemView.findViewById(R.id.NameTeacher);
        nameTextView.setText(currentTeacher.getName());

        TextView positionTextView = listItemView.findViewById(R.id.PostTeacher);
        positionTextView.setText(currentTeacher.getPost());


        return listItemView;
    }
}

