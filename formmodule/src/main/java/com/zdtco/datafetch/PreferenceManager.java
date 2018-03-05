package com.zdtco.datafetch;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by G1494458 on 2017/03/07.
 */

public class PreferenceManager {
    public static final String PREFERENCE_NAME = "saveInfo";
    private static SharedPreferences sharedPreferences;
    private static PreferenceManager preferenceManager;
    private static SharedPreferences.Editor editor;

    private static final String KEY_USER_WORK_NO = "key_user_work_no";
    private static final String KEY_LAST_UPDATE_TIME = "key_last_update_time";
    private static final String KEY_STAY_TIME = "key_stay_time";
    private static final String KEY_CONTINUOUS_LOGIN_DAYS = "key_continuous_login_days";

    private PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized PreferenceManager getInstance(Context context) {
        if (preferenceManager == null) {
            preferenceManager = new PreferenceManager(context);
        }
        return preferenceManager;
    }

    public void setUserWorkNo(String workNo) {
        editor.putString(KEY_USER_WORK_NO, workNo);
        editor.apply();
    }

    public String getUserWorkNo() {
        return sharedPreferences.getString(KEY_USER_WORK_NO, "");
    }

    public void setLastUpdateTime(String time) {
        editor.putString(KEY_LAST_UPDATE_TIME, time);
        editor.apply();
    }

    public String getLastUpdateTime() {
        return sharedPreferences.getString(KEY_LAST_UPDATE_TIME, "");
    }

    public void setStayTime(int time) {
        editor.putInt(KEY_STAY_TIME, time);
        editor.apply();
    }

    public int getStayTime() {
        return sharedPreferences.getInt(KEY_STAY_TIME, 0);
    }

    public void setContinuousLoginDays(String time) {
        editor.putString(KEY_CONTINUOUS_LOGIN_DAYS, time);
        editor.apply();
    }

    public String getContinuousLoginDays() {
        return sharedPreferences.getString(KEY_CONTINUOUS_LOGIN_DAYS, "");
    }

}
