package smg.mironov.ksuschedule;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView; // Импортируем SearchView
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

/**
 * Класс {@link TeachersActivity} отвечает за отображение списка преподавателей и информацию о каждом преподавателе.
 *
 * @version 1.0
 */
public class TeachersActivity extends AppCompatActivity {

    /** Список преподавателей */
    private ListView listView;
    /** Адаптер для отображения преподавателей */
    private TeacherAdapter adapter;
    /** Список объектов TeacherDto */
    private List<TeacherDto> teacherList;

    /** Токен аутентификации */
    private String token;

    private TextView noResultsText;

    /** SearchView для поиска преподавателей */
    private androidx.appcompat.widget.SearchView searchView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        // Проверяем, какая тема была выбрана
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_teacher_screen);

        // Инициализация SearchView
        searchView = findViewById(R.id.search_view);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnBackground, typedValue, true);
        searchEditText.setTextColor(typedValue.data);

        noResultsText = findViewById(R.id.no_results_text);

        listView = findViewById(R.id.list_teachers);



        teacherList = new ArrayList<>();

        token = "Bearer " + preferences.getString("auth_token", null);


        adapter = new TeacherAdapter(this, teacherList, (fullName, profileImageView) -> {
            // Вызываем метод для поиска преподавателя по ФИО
            getUserByFullName(token, fullName, profileImageView);
        }, token);

        listView.setAdapter(adapter);

        ImageView navButton1 = findViewById(R.id.home_icon);
        ImageView navButton2 = findViewById(R.id.profile_icon);

        navButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToScreen1();
            }
        });

        navButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToScreen2();
            }
        });

        // Настройка слушателя для SearchView
        setupSearch();

        fetchTeachersFromServer();
    }

    private PopupWindow popupWindow;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Фильтрация при отправке запроса
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Фильтрация при изменении текста
                adapter.getFilter().filter(newText);
                // Проверка на пустую строку
                if (newText.isEmpty()) {
                    noResultsText.setVisibility(View.GONE);
                } else if (adapter.isEmpty()) {
                    noResultsText.setVisibility(View.VISIBLE);
                } else {
                    noResultsText.setVisibility(View.GONE);
                }

                return true;
            }
        });
        // Дополнительные настройки SearchView
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
    }




    private void getUserByFullName(String token, String fullName, final ImageView profileImageView) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUserByFullName(token, fullName);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    // Проверяем роль пользователя
                    if ("TEACHER".equals(user.getRole())) {
                        fetchTeacherPostAndShowPopup(fullName, user, profileImageView);
                    } else {
                        Toast.makeText(TeachersActivity.this, "Пользователь не является преподавателем", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Преподаватель не зарегистрирован
                    fetchTeacherPostAndShowPopup(fullName, null, profileImageView);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(TeachersActivity.this, "Ошибка соединения: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchTeacherPostAndShowPopup(String fullName, User user, ImageView profileImageView) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<TeacherDto> call = apiService.getTeacherByName(fullName); // Предполагается, что API возвращает строку с должностью

        call.enqueue(new Callback<TeacherDto>() {
            @Override
            public void onResponse(Call<TeacherDto> call, Response<TeacherDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TeacherDto teacherDto = response.body();

                    String post = teacherDto.getPost();

                    if (user != null) {
                        showInfo(user, profileImageView, post);
                    } else {
                        showUnregisteredTeacherPopup(fullName, profileImageView, post);
                    }
                }

            }

            @Override
            public void onFailure(Call<TeacherDto> call, Throwable t) {
                String post = "Должность не указана";

                if (user != null) {
                    showInfo(user, profileImageView, post);
                } else {
                    showUnregisteredTeacherPopup(fullName, profileImageView, post);
                }
            }
        });
    }



    /**
     * Отображение информации о преподавателе.
     */
    private void showTeacherDetails(User user, ImageView profileImageView, String post) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.teacher_info_popup, null);

        TextView popupLastName = popupView.findViewById(R.id.teacherLastname);
        TextView popupFirstName = popupView.findViewById(R.id.teacherFirstName);
        TextView popupMiddleName = popupView.findViewById(R.id.teacherMiddleName);
        TextView popupPost = popupView.findViewById(R.id.post);
        ImageView popupPhoto = popupView.findViewById(R.id.teacherPhoto);
        TextView facultyView = popupView.findViewById(R.id.userGroupDirection);
        TextView departmentView = popupView.findViewById(R.id.userGroupProfileTitle);

        // Установка основных данных
        popupLastName.setText(user.getLastName());
        popupFirstName.setText(user.getFirstName());
        popupMiddleName.setText(user.getMiddleName());
        popupPost.setText(post);

        // Извлекаем данные из info
        String[] parsedInfo = parseInfo(user.getInfo());
        String faculty = parsedInfo[0];
        String department = parsedInfo[1];

        // Устанавливаем факультет
        if (!faculty.equals("Факультет не указан")) {
            facultyView.setText(faculty);
            facultyView.setVisibility(View.VISIBLE);
        } else {
            facultyView.setVisibility(View.GONE);
        }

        // Устанавливаем кафедру
        if (!department.equals("Кафедра не указана")) {
            departmentView.setText(department);
            departmentView.setVisibility(View.VISIBLE);
        } else {
            departmentView.setVisibility(View.GONE);
        }

        // Загружаем фото, если оно существует
        if (user.getPhoto() != null && user.getPhoto().getUrl() != null) {
            String photoUrl = user.getPhoto().getUrl();
            if (photoUrl.startsWith("/root/app/uploads")) {
                // Заменяем локальный путь на доступный HTTP-URL
                photoUrl = photoUrl.replace("/root/app/uploads", "http://24schedule.ru/uploads");
            }

            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(popupPhoto);

        } else {
            Log.w("PhotoLoader", "Photo or URL is null for user: " + user.getLastName());
            popupPhoto.setImageResource(R.drawable.placeholder_image); // Фото по умолчанию
        }


        showPopup(popupView, profileImageView);
    }

    private void showUnregisteredTeacherPopup(String fullName, ImageView profileImageView, String post) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.teacher_info_popup, null);

        TextView popupLastName = popupView.findViewById(R.id.teacherLastname);
        TextView popupFirstName = popupView.findViewById(R.id.teacherFirstName);
        popupFirstName.setVisibility(View.GONE);
        TextView popupMiddleName = popupView.findViewById(R.id.teacherMiddleName);
        popupMiddleName.setVisibility(View.GONE);
        TextView popupPost = popupView.findViewById(R.id.post);
        ImageView popupPhoto = popupView.findViewById(R.id.teacherPhoto);

        // Устанавливаем минимальные данные
        popupLastName.setText(fullName);
        popupPost.setText(post);

        // Устанавливаем фото по умолчанию
        popupPhoto.setImageResource(R.drawable.placeholder_image);

        showPopup(popupView, profileImageView);
    }

    private void showPopup(View popupView, ImageView profileImageView) {
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(profileImageView, Gravity.CENTER, 0, 0);

        // Закрытие окна
        ImageView closeButton = popupView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> popupWindow.dismiss());
    }

    private void showInfo(User user, ImageView profileImageView, String post) {
        Log.d(TAG, "Showing info for user: " + user.toString());
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.teacher_info_popup, null);

        TextView popupLastName = popupView.findViewById(R.id.teacherLastname);
        TextView popupFirstName = popupView.findViewById(R.id.teacherFirstName);
        TextView popupMiddleName = popupView.findViewById(R.id.teacherMiddleName);
        TextView popupPost = popupView.findViewById(R.id.post);
        TextView popupInfo = popupView.findViewById(R.id.teacherInfo);
        ImageView popupPhoto = popupView.findViewById(R.id.teacherPhoto);

        // Устанавливаем текстовые данные
        popupLastName.setText(user.getLastName());
        popupFirstName.setText(user.getFirstName());
        popupMiddleName.setText(user.getMiddleName());
        popupPost.setText(post);
        popupInfo.setText(user.getInfo());

        // Загружаем фото
        fetchTeacherPhotoFromServer(user.getId(), popupPhoto, profileImageView);

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        addDimBackground(popupWindow);

        popupWindow.showAtLocation(profileImageView, Gravity.CENTER, 0, 0);

        ImageView closeButton = popupView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            profileImageView.setEnabled(true);
            profileImageView.setVisibility(View.VISIBLE);
            removeDimBackground();
        });
    }

    /**
     * Метод для добавления затемненного фона при открытии всплывающего окна.
     *
     * @param popupWindow объект {@link PopupWindow}
     */
    private void addDimBackground(PopupWindow popupWindow) {
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content).getRootView();
        View dimView = new View(this);
        dimView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dimView.setBackgroundColor(Color.parseColor("#80000000"));
        dimView.setTag("dim_view"); // Устанавливаем тег для идентификации
        root.addView(dimView);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                root.removeView(dimView);
            }
        });
    }

    /**
     * Метод для удаления затемненного фона при закрытии всплывающего окна.
     */
    private void removeDimBackground() {
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content).getRootView();
        View dimView = root.findViewWithTag("dim_view");
        if (dimView != null) {
            root.removeView(dimView);
        }
    }

    /**
     * Метод для получения фотографии преподавателя с сервера и отображения в ImageView.
     *
     * @param userId идентификатор пользователя
     * @param popupPhoto ImageView для отображения фотографии во всплывающем окне
     * @param profileImageView ImageView для отображения фотографии в списке (если нужно)
     */
    private void fetchTeacherPhotoFromServer(Long userId, final ImageView popupPhoto, final ImageView profileImageView) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getProfileImage(token, userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Декодируем поток данных в Bitmap
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());

                        if (bitmap != null) {
                            // Устанавливаем изображение в ImageView
                            popupPhoto.setImageBitmap(bitmap);

                            if (profileImageView != null) {
                                profileImageView.setImageBitmap(bitmap);
                            }

                            // Сохраняем изображение во внутреннее хранилище
                            //saveImageToInternalStorage(bitmap, userId);
                        } else {
                            // Устанавливаем фото по умолчанию при ошибке декодирования
                            popupPhoto.setImageResource(R.drawable.placeholder_image);
                            if (profileImageView != null) {
                                profileImageView.setImageResource(R.drawable.placeholder_image);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("PhotoLoader", "Ошибка при декодировании изображения: " + e.getMessage());
                        popupPhoto.setImageResource(R.drawable.placeholder_image);
                    }
                } else {
                    Log.e("PhotoLoader", "Ошибка загрузки изображения: " + response.code());
                    popupPhoto.setImageResource(R.drawable.placeholder_image);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("PhotoLoader", "Ошибка сети: " + t.getMessage());
                popupPhoto.setImageResource(R.drawable.placeholder_image);
            }
        });
    }




    /**
     * Метод для получения списка преподавателей с сервера.
     */
    private void fetchTeachersFromServer() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://24schedule.ru:80/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<TeacherDto>> call = apiService.getAllTeachers();
        call.enqueue(new Callback<List<TeacherDto>>() {
            @Override
            public void onResponse(Call<List<TeacherDto>> call, Response<List<TeacherDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    refreshTokenIfNeeded(response);
                    teacherList.clear();
                    teacherList.addAll(response.body());

                    // Обновляем данные адаптера
                    adapter.updateData(teacherList);
                } else {
                    Toast.makeText(TeachersActivity.this, "Ошибка при получении данных: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TeacherDto>> call, Throwable t) {
                Toast.makeText(TeachersActivity.this, "Ошибка соединения: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Метод для переключения на главный экран.
     */
    private void switchToScreen1() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Метод для переключения на экран профиля.
     */
    private void switchToScreen2() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Метод для обновления токена аутентификации, если это необходимо.
     *
     * @param response объект {@link Response}, содержащий ответ сервера
     */
    private void refreshTokenIfNeeded(Response<?> response) {
        String newToken = response.headers().get("Authorization");
        if (newToken != null && newToken.startsWith("Bearer ")) {
            SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("auth_token", newToken.substring(7));
            editor.apply();
        }
    }


    /**
     * Метод для извлечения факультета, кафедры и дополнительной информации из строки info.
     *
     * @param info строка формата "Факультет:\n<Факультет>\nКафедра:\n<Кафедра>\nДоп.информация:\n<Доп.информация>"
     * @return массив строк: [факультет, кафедра, дополнительная информация]
     */
    private String[] parseInfo(String info) {
        if (info == null || info.trim().isEmpty()) {
            return new String[]{"Факультет не указан", "Кафедра не указана", "Доп. информация отсутствует"};
        }

        String faculty = "Факультет не указан";
        String department = "Кафедра не указана";
        String additionalInfo = "Доп. информация отсутствует";

        // Разделяем строку по строкам
        String[] lines = info.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.startsWith("Факультет:") && i + 1 < lines.length) {
                faculty = lines[i + 1].trim();
            } else if (line.startsWith("Кафедра:") && i + 1 < lines.length) {
                department = lines[i + 1].trim();
            } else if (line.startsWith("Доп.информация:") && i + 1 < lines.length) {
                additionalInfo = lines[i + 1].trim();
            }
        }

        return new String[]{faculty, department, additionalInfo};
    }



}
