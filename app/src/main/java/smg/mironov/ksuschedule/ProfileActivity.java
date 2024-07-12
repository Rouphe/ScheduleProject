package smg.mironov.ksuschedule;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Models.GroupDto;
import smg.mironov.ksuschedule.Models.TeacherDto;

public class ProfileActivity extends AppCompatActivity {

    private TextView userLastname, userFirstName, userMiddleName, userGroupNumber, userGroupDirection, userGroupProfile, userGroupProfileTitle;
    private View additionalInfoContainer;
    private ImageView chevronDown, logoutButton, settingsButton, profilePhoto, addPhotoButton;
    private ConstraintLayout profileCap;
    private int originalHeight;
    private LinearLayout editButton;

    private String token;
    private String userEmail;

    private boolean isExpanded = false;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        token = "Bearer " + preferences.getString("auth_token", null);
        userEmail = preferences.getString("user_email", null);

        // Настройка кнопок навигационной панели
        ImageView navButton1 = findViewById(R.id.teachers_icon);
        ImageView navButton2 = findViewById(R.id.home_icon);


        userLastname = findViewById(R.id.userLastname);
        userFirstName = findViewById(R.id.userFirstName);
        userMiddleName = findViewById(R.id.userMiddleName);
        userGroupNumber = findViewById(R.id.userGroupNumber);
        additionalInfoContainer = findViewById(R.id.additional_info_container);
        chevronDown = findViewById(R.id.chevron_down);
        userGroupDirection = findViewById(R.id.userGroupDirection);
        userGroupProfile = findViewById(R.id.userGroupProfile);
        logoutButton = findViewById(R.id.logout_icon);
        settingsButton = findViewById(R.id.settings_icon);
        profilePhoto = findViewById(R.id.profilePhoto);
        addPhotoButton = findViewById(R.id.addPhoto);
        userGroupProfileTitle = findViewById(R.id.userGroupProfileTitle);
        profileCap = findViewById(R.id.profile_cap);
        chevronDown = findViewById(R.id.chevron_down);
        editButton = findViewById(R.id.editButton);

        loadUserData();


        // Назначаем обработчик на кнопку изменения фото
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        chevronDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAdditionalInfo();
                toggleAnimation();
            }
        });

        navButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика переключения на первый экран
                switchToTeachersScreen();
            }
        });

        navButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика переключения на второй экран
                switchToMain();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToSettings();
            }
        });

        profileCap.post(new Runnable() {
            @Override
            public void run() {
                originalHeight = profileCap.getHeight();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToEdit();
            }
        });

    }

    private void switchToEdit() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    private void toggleAnimation() {
        ValueAnimator valueAnimator;
        if (isExpanded) {
            valueAnimator = ValueAnimator.ofInt(profileCap.getHeight(), originalHeight);
        } else {
            valueAnimator = ValueAnimator.ofInt(profileCap.getHeight(), (int) (originalHeight * 1.50));
        }

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = profileCap.getLayoutParams();
                layoutParams.height = (int) animation.getAnimatedValue();
                profileCap.setLayoutParams(layoutParams);
            }
        });

        valueAnimator.setDuration(300);
        valueAnimator.start();
        isExpanded = !isExpanded;
    }

    // Метод для открытия галереи для выбора изображения
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите фото профиля"), PICK_IMAGE_REQUEST);
    }

    private void switchToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Удаление фото профиля
        deleteProfileImage();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void deleteProfileImage() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("profile_images", Context.MODE_PRIVATE);
        File path = new File(directory, "profile_photo.jpg");
        if (path.exists()) {
            path.delete();
        }
    }


    private void switchToTeachersScreen() {
        // Логика переключения на экран преподавателей
        Intent intent = new Intent(this, TeachersActivity.class);
        startActivity(intent);
    }

    private void switchToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    // Метод для обработки выбора изображения из галереи
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profilePhoto.setImageBitmap(bitmap);

                // Отправляем изображение на сервер
                uploadImageToServer(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        String lastName = sharedPreferences.getString("user_lastName", "Фамилия");
        String firstName = sharedPreferences.getString("user_firstName", "Имя");
        String middleName = sharedPreferences.getString("user_middleName", "Отчество");
        String groupNumber = sharedPreferences.getString("user_groupNumber", "100");
        String role = sharedPreferences.getString("user_role", "STUDENT");

        userLastname.setText(lastName);
        userFirstName.setText(firstName);
        userMiddleName.setText(middleName);
        userGroupNumber.setText(groupNumber);

        Bitmap profileBitmap = loadImageFromInternalStorage();
        if (profileBitmap == null) {
            fetchProfileImageFromServer();
        } else {
            profilePhoto.setImageBitmap(profileBitmap);
        }

        if (role.equals("STUDENT")) {
            userGroupNumber.setText(groupNumber);
            fetchGroupInfo(groupNumber);
            editButton.setVisibility(View.GONE);
        } else {
            userGroupNumber.setVisibility(View.INVISIBLE);
            fetchTeacherInfo(lastName + " " + firstName.charAt(0) + "." + middleName.charAt(0) + ".");
        }
    }


    private void toggleAdditionalInfo() {
        if (additionalInfoContainer.getVisibility() == View.GONE) {
            additionalInfoContainer.setVisibility(View.VISIBLE);
            chevronDown.setImageResource(R.drawable.chevron_up);
        } else {
            additionalInfoContainer.setVisibility(View.GONE);
            chevronDown.setImageResource(R.drawable.chevron_down_white);
        }
    }

    private void fetchGroupInfo(String groupNumber) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<GroupDto> call = apiService.getGroupByNumber(token, groupNumber);

        call.enqueue(new Callback<GroupDto>() {
            @Override
            public void onResponse(Call<GroupDto> call, Response<GroupDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GroupDto groupDto = response.body();


                    if (groupDto.getProfile() == null) {
                        userGroupProfile.setVisibility(View.GONE);
                        userGroupProfileTitle.setVisibility(View.GONE);
                    } else {
                        userGroupProfile.setText(groupDto.getProfile());
                    }

                    if (groupDto.getProfile() == null && groupDto.getDirection() == null) {
                        userGroupDirection.setText("Информация не найдена.");
                    }

                    userGroupDirection.setText(groupDto.getDirection());

                }
            }

            @Override
            public void onFailure(Call<GroupDto> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Ошибка загрузки информации", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTeacherInfo(String name) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<TeacherDto> call = apiService.getTeacherByName(token, name);

        call.enqueue(new Callback<TeacherDto>() {
            @Override
            public void onResponse(Call<TeacherDto> call, Response<TeacherDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TeacherDto teacherDto = response.body();

                    TextView groupLabel = findViewById(R.id.group_label);
                    groupLabel.setText("Должность");
                    userGroupNumber.setText(teacherDto.getPost());
                    userGroupNumber.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<TeacherDto> call, Throwable t) {

            }
        });
    }

    // Метод для отправки изображения на сервер
    private void uploadImageToServer(Uri imageUri) {
        String filePath = getRealPathFromURI(imageUri);
        if (filePath == null) {
            Toast.makeText(this, "Невозможно получить путь к файлу", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.uploadProfileImage(token, userEmail, body); // Передаем email пользователя

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Фото профиля успешно обновлено", Toast.LENGTH_SHORT).show();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        saveImageToInternalStorage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Ошибка загрузки фото профиля", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProfileImageFromServer() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getProfileImage(token, userEmail); // предполагается, что API предоставляет такой метод

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    profilePhoto.setImageBitmap(bitmap);
                    saveImageToInternalStorage(bitmap);
                } else {
                    Toast.makeText(ProfileActivity.this, "Ошибка загрузки фото профиля", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImageToInternalStorage(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("profile_images", Context.MODE_PRIVATE);
        File path = new File(directory, "profile_photo.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap loadImageFromInternalStorage() {
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("profile_images", Context.MODE_PRIVATE);
            File path = new File(directory, "profile_photo.jpg");
            return BitmapFactory.decodeStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


}
