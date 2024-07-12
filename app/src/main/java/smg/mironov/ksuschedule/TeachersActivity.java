package smg.mironov.ksuschedule;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Adapters.TeacherAdapter;
import smg.mironov.ksuschedule.Models.TeacherDto;
import smg.mironov.ksuschedule.Models.User;

public class TeachersActivity extends AppCompatActivity {

    private ListView listView;
    private TeacherAdapter adapter;
    private List<TeacherDto> teacherList;

    private String token;
    private String post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_teacher_screen);

        // Инициализация ListView и адаптера
        listView = findViewById(R.id.list_teachers);
        teacherList = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        token = "Bearer " + preferences.getString("auth_token", null);

        adapter = new TeacherAdapter(this, teacherList, new TeacherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int teacherId, ImageView infoTeacher) {
                // Обработка клика на преподавателе
                getUserByTeacherId(teacherId, infoTeacher);
                getTeacherById(teacherId);
            }
        });

        listView.setAdapter(adapter);

        // Инициализация кнопок навигационной панели
        ImageView navButton1 = findViewById(R.id.home_icon);
        ImageView navButton2 = findViewById(R.id.profile_icon);

        navButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика переключения на первый экран
                switchToScreen1();
            }
        });

        navButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика переключения на второй экран
                switchToScreen2();
            }
        });

        // Получение данных с сервера
        fetchTeachersFromServer();
    }

    private void getUserByTeacherId(int teacherId, final ImageView infoTeacher) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getTeacherUserByTeacherId(token, teacherId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    // Передача id преподавателя и ImageView в метод getPhotoById
                    getPhotoById(user.getTeacherId(), infoTeacher);

                    showInfo(user, infoTeacher);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Обработка ошибки
            }
        });
    }

    private void showInfo(User user, ImageView infoTeacher) {
        Log.d(TAG, "Showing info for user: " + user.toString());
        // Inflate the popup window layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.teacher_info_popup, null);

        // Initialize views inside popup
        TextView popupLastName = popupView.findViewById(R.id.teacherLastname);
        TextView popupFirstName = popupView.findViewById(R.id.teacherFirstName);
        TextView popupMiddleName = popupView.findViewById(R.id.teacherMiddleName);
        TextView popupPost = popupView.findViewById(R.id.post);
        TextView popupInfo = popupView.findViewById(R.id.teacherInfo);
        ImageView popupPhoto = popupView.findViewById(R.id.teacherPhoto);

        // Set user information in popup views
        popupLastName.setText(user.getLastName());
        popupFirstName.setText(user.getFirstName());
        popupMiddleName.setText(user.getMiddleName());
        popupPost.setText(post);
        popupInfo.setText(user.getInfo());

        // Create the PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Set the background of PopupWindow to a transparent color
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Allow outside touch to dismiss the popup
        popupWindow.setOutsideTouchable(true);

        // Set the popup window location relative to infoTeacher
        int[] location = new int[2];
        infoTeacher.getLocationOnScreen(location);
        int offsetX = 0; // adjust as needed
        int offsetY = 0; // adjust as needed
        popupWindow.showAtLocation(infoTeacher, Gravity.NO_GRAVITY, location[0] + offsetX, location[1] + offsetY);

        // Dim the background (optional)
        View container = (View) popupWindow.getContentView().getParent();
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) container.getLayoutParams();
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        getWindowManager().updateViewLayout(container, layoutParams);

        // Close button inside the popup
        ImageView closeButton = popupView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }



    private void getPhotoById(int userId, final ImageView popupPhoto) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ResponseBody> responseBodyCall = apiService.getTeacherPhoto(token, userId);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null){
                    try {
                        // Получаем URL фото из тела ответа
                        String photoUrl = response.body().string(); // assuming the URL is in the response body

                        // Устанавливаем фото в ImageView с помощью Glide
                        Glide.with(TeachersActivity.this)
                                .load(photoUrl)
                                .into(popupPhoto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Обработка ошибки
                Toast.makeText(TeachersActivity.this, "Ошибка при загрузке фото: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getTeacherById(int teacherId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Запрос на получение информации о преподавателе
        Call<TeacherDto> teacherCall = apiService.getTeacherById(token, teacherId);
        teacherCall.enqueue(new Callback<TeacherDto>() {
            @Override
            public void onResponse(Call<TeacherDto> call, Response<TeacherDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TeacherDto teacher = response.body();

                    // Показать информацию о преподавателе в popup
                    post = teacher.getPost();

                } else {
                    // Обработка ошибки при получении данных о преподавателе
                    Toast.makeText(TeachersActivity.this, "Ошибка при получении данных о преподавателе", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeacherDto> call, Throwable t) {
                // Обработка ошибки
                Toast.makeText(TeachersActivity.this, "Ошибка при выполнении запроса: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchTeachersFromServer() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://77.232.128.111:8081/") // Замените на ваш базовый URL
                .addConverterFactory(GsonConverterFactory.create()).build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<TeacherDto>> call = apiService.getAllTeachers();
        call.enqueue(new Callback<List<TeacherDto>>() {
            @Override
            public void onResponse(Call<List<TeacherDto>> call, Response<List<TeacherDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    teacherList.clear();
                    teacherList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    // Логика обработки кода ошибки
                    Toast.makeText(TeachersActivity.this, "Ошибка при получении данных: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TeacherDto>> call, Throwable t) {
                // Логика обработки ошибки соединения
                Toast.makeText(TeachersActivity.this, "Ошибка соединения: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void switchToScreen1() {
        // Логика переключения на первый экран
        // Например, запуск новой активности:
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void switchToScreen2() {
        // Логика переключения на второй экран
        // Например, запуск новой активности:
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
