package salam.gohajj.id.menu;

/**
 * Created by bayem on 4/10/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splashscreen);

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

    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public boolean hasPermissions(@NonNull String... permissions) {
//        for (String permission : permissions)
//            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permission))
//                return false;
//        return true;
//    }

}