package com.anzo.igniteacademy.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    public SharedPreferences preferences;
    public static final String PREFERENCE_NAME = "user_pref";

    public static final String USER_ID = "u_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_MOBILE_NUMBER = "user_mobile_number";

    public static final String IS_LOGIN = "is_login";
    public static final String COURSE_FEE ="course_fee";

    public UserPreferences(Context context) {
        preferences = (SharedPreferences) context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void setData(String param, Object value) {
        if (value instanceof String) {
            preferences.edit().putString(param, (String) value).apply();
        } else if (value instanceof Integer) {
            preferences.edit().putInt(param, (int) value).apply();
        } else if (value instanceof Boolean) {
            preferences.edit().putBoolean(param, (boolean) value).apply();
        }
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

    public String getUsername() {
        String value = preferences.getString(USER_NAME, "");
        return value;
    }

    public String getUserId() {
        String value = preferences.getString(USER_ID, "");
        return value;
    }
    public String getCourseFee(){
        String value = preferences.getString(COURSE_FEE, "");
        return value;
    }

    public String getUserMobileNumber() {
        String value = preferences.getString(USER_MOBILE_NUMBER, "");
        return value;
    }

    public String getUserEmail() {
        String value = preferences.getString(USER_EMAIL, "");
        return value;
    }

    public void setLogin(boolean status) {
        preferences.edit().putBoolean(IS_LOGIN, status).apply();
    }

    public boolean isUserLoggedIn() {
        return preferences.getBoolean(IS_LOGIN, false);
    }
}
