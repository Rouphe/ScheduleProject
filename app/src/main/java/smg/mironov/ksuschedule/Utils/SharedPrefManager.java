package smg.mironov.ksuschedule.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_PARITY = "parity";
    private static final String KEY_SUBGROUP_NUMBER = "subgroup_number";
    private static final String KEY_GROUP_NUMBER = "group_number";

    private SharedPreferences sharedPreferences;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
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
}

