package smg.mironov.ksuschedule.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_PARITY = "ЧИСЛИТЕЛЬ";
    private static final String KEY_SUBGROUP_NUMBER = "109.2";
    private static final String KEY_GROUP_NUMBER = "109";
    private static final String KEY_FIRST_TIME_USER = "first_time_user";


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

    public void setParity(String parity) {
        sharedPreferences.edit().putString(KEY_PARITY, parity).apply();
    }

    public String getParity() {
        return sharedPreferences.getString(KEY_PARITY, "ЧИСЛИТЕЛЬ");
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

