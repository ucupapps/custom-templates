package salam.gohajj.custom.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ucup on 25/08/17.
 */

public class Utilities {
    public static void putPref(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static void ShowLog(String message, String value){
        Log.e("GoHajj_Debug", message+" : "+value);
    }

    public static void ShowToast(Activity activity, String message, String value) {
        Toast.makeText(activity, message+" : "+value, Toast.LENGTH_SHORT).show();
    }
}
