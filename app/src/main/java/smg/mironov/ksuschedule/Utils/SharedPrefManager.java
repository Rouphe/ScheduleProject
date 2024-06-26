package smg.mironov.ksuschedule.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_PARITY = "ЧИСЛИТЕЛЬ";
    private static final String KEY_SUBGROUP_NUMBER = "subgroup_number";
    private static final String KEY_GROUP_NUMBER = "group_number";
    private static final String KEY_FIRST_TIME_USER = "";
    private static final String KEY_ROLE = "Студент";
    private static final String KEY_TEACHER_NAME = "Преподаватель";


    private static SharedPrefManager instance;
    private SharedPreferences sharedPreferences;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public boolean isFirstTimeUser() {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME_USER, true);
    }

    public void setTeacherName(String teacherName) {
        sharedPreferences.edit().putString(KEY_TEACHER_NAME, teacherName).apply();
    }

    public String getTeacherName() {
        return sharedPreferences.getString(KEY_TEACHER_NAME, "");
    }

    public void setRole(String role) {
        sharedPreferences.edit().putString(KEY_ROLE, role).apply();
    }

    public String getRole() {
        return  sharedPreferences.getString(KEY_ROLE, "");
    }

    public void setParity(String parity) {
        sharedPreferences.edit().putString(KEY_PARITY, parity).apply();
    }

    public String getParity() {
        return sharedPreferences.getString(KEY_PARITY, "");
    }

    public void setSubgroupNumber(String subgroupNumber) {
        sharedPreferences.edit().putString(KEY_SUBGROUP_NUMBER, subgroupNumber).apply();
    }

    public String getSubgroupNumber() {
        return sharedPreferences.getString(KEY_SUBGROUP_NUMBER, "");
    }

    public void setGroupNumber(String groupNumber) {
        sharedPreferences.edit().putString(KEY_GROUP_NUMBER, groupNumber).apply();
    }

    public String getGroupNumber() {
        return sharedPreferences.getString(KEY_GROUP_NUMBER, "");
    }

    public void setFirstTimeUser(boolean b) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_TIME_USER, false);
    }
}

