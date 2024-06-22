package smg.mironov.ksuschedule.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;


import java.util.List;

import smg.mironov.ksuschedule.R;
import smg.mironov.ksuschedule.Teacher;

public class TeacherAdapter extends ArrayAdapter<Teacher> {

    public TeacherAdapter(Context context, List<Teacher> teachers) {
        super(context, 0, teachers);
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_info_teacher, parent, false);
        }

        Teacher currentTeacher = getItem(position);

        TextView nameTextView = listItemView.findViewById(R.id.NameTeacher);
        nameTextView.setText(currentTeacher.getName());

        TextView positionTextView = listItemView.findViewById(R.id.PostTeacher);
        positionTextView.setText(currentTeacher.getPost());


        return listItemView;
    }
}

