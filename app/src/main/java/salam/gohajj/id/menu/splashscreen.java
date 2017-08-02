package salam.gohajj.id.menu;

/**
 * Created by bayem on 4/10/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import java.util.Locale;

import salam.gohajj.id.R;

public class splashscreen extends Activity {

    //Set waktu lama splashscreen
    private static int splashInterval = 2000;
//    private static final String[] requiredPermissions = new String[]{
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.SEND_SMS,
//            Manifest.permission.READ_CONTACTS,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            /* ETC.. */
//    };
    private SQLiteDatabase database;
    private static Locale myLocale;

    //Shared Preferences Variables
    private static final String Locale_Preference = "Locale Preference";
    private static final String Locale_KeyValue = "Saved Locale";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splashscreen);
        sharedPreferences = getSharedPreferences(Locale_Preference, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
//        if (Build.VERSION.SDK_INT > 22 && !hasPermissions(requiredPermissions)) {
//            Toast.makeText(this, "Please grant all permissions", Toast.LENGTH_LONG).show();
//            //permission
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                    Uri.fromParts("package", getPackageName(), null));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }else {
        createDatabase();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Cursor mCount= database.rawQuery("select count(*) from loader where status=1", null);
                    mCount.moveToFirst();
                    int count= mCount.getInt(0);
                    if(count > 0) {
                        Intent i = new Intent(splashscreen.this, panduan.class);
                        startActivity(i);
                    }else {
                        String query = "INSERT INTO badge (jumlah) VALUES(0);";
                        database.execSQL(query);
                        Intent i = new Intent(splashscreen.this, AndroidImageSlider.class);
                        startActivity(i);
                    }

                    //jeda selesai Splashscreen
                    this.finish();
                }

                private void finish() {
                    // TODO Auto-generated method stub

                }
            }, splashInterval);
//        }

    };

    protected void createDatabase(){
        database=openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS loader(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, status INTEGER);");
        database.execSQL("CREATE TABLE IF NOT EXISTS badge(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, jumlah INTEGER);");
        database.execSQL("CREATE TABLE IF NOT EXISTS play(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, status INTEGER);");
        database.execSQL("CREATE TABLE IF NOT EXISTS language(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, kode TEXT,bahasa TEXT);");
        Cursor mCoun= database.rawQuery("select count(*) from play", null);
        mCoun.moveToFirst();
        int coun= mCoun.getInt(0);
        if(coun == 0) {
            String query = "INSERT INTO play (status) VALUES(1);";
            database.execSQL(query);
        }

        //language
        Cursor lg= database.rawQuery("select count(*) from language", null);
        lg.moveToFirst();
        int cn= lg.getInt(0);
        if(cn == 0) {
            String query = "INSERT INTO language (kode,bahasa) VALUES('id','Indonesia');";
            database.execSQL(query);
            changeLocale("id");
        }else{
            Cursor mCou= database.rawQuery("select kode from language where id=1", null);
            mCou.moveToFirst();
            String status= mCou.getString(0);
            changeLocale(status);
        }

    }

    public void changeLocale(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);//Set Selected Locale
        saveLocale(lang);//Save the selected locale
        Locale.setDefault(myLocale);//set new locale as default
        Configuration config = new Configuration();//get Configuration
        config.locale = myLocale;//set config locale as selected locale
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());//Update the config
//        updateTexts();//Update texts according to locale
    }

    public void saveLocale(String lang) {
        editor.putString(Locale_KeyValue, lang);
        editor.commit();
    }
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public boolean hasPermissions(@NonNull String... permissions) {
//        for (String permission : permissions)
//            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permission))
//                return false;
//        return true;
//    }

}