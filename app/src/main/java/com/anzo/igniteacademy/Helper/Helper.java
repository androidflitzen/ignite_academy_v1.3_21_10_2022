package com.anzo.igniteacademy.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Helper {

    public static final String defaultFormat = "yyyy-MM-dd HH:mm:ss";
    public static final String defaultDateFormat = "yyyy-MM-dd";
    public static final String defaultTimeFormat = "HH:mm:ss";

    private static final String amountFormatRoundOf = "#,##,###";
    private static final String amountFormat = "#,##,###.00";

    public static void LOG(String key, String value) {
        Log.e(key, "-" + value);
    }

    public static String getCurrentDateTime(String formate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                formate, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static boolean isInternetAvailable(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static boolean isNullOrEmpty(String value) {
        if (value == null)
            return true;
        value = value.trim();
        if (value.isEmpty())
            return true;
        if (value.equals("null"))
            return true;
        return false;
    }

    public static Date convertStringToDate(String value) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat(defaultFormat);
        Date date = null;
        try {
            date = inputFormat.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String changeDateFormat(String time) {
        if(isNullOrEmpty(time))
            return "";
        String inputPattern = defaultFormat;
        String outputPattern = "dd MMM yyyy hh:mm a";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String changeDateFormat(String time, String oFormat) {//2017-07-14 24:00:00
        if(isNullOrEmpty(time))
            return "";
        String inputPattern = defaultFormat;
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(oFormat);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String changeDateFormat(String time, String inputFormat, String outputFormat) {//2017-07-14 24:00:00
        if(isNullOrEmpty(time))
            return "";
        SimpleDateFormat dateInputFormat = new SimpleDateFormat(inputFormat);
        SimpleDateFormat dateOutputFormat = new SimpleDateFormat(outputFormat);

        Date date = null;
        String str = null;

        try {
            date = dateInputFormat.parse(time);
            str = dateOutputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String amountRoundOf(double value) {
        DecimalFormat dFormat = new DecimalFormat(amountFormatRoundOf);
        return dFormat.format(value);
    }

    public static String amountFormat(double value) {
        DecimalFormat dFormat = new DecimalFormat(amountFormat);
        return dFormat.format(value);
    }

    public static void hideSoftInput(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }
}
