package com.garudatekno.jemaah.menu;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.BackgroundService;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.MainActivity;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;


public class sai extends AppCompatActivity {

    private SQLiteHandler db;
    private SessionManager session;

    protected TextView C_1,C_2,C_3,C_4,C_5,C_6,C_7;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sai);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // HEADER
        LinearLayout menu_panduan=(LinearLayout) findViewById(R.id.menu_panduan);
        TextView txt_panduan=(TextView) findViewById(R.id.txt_panduan);
        LinearLayout menu_doa=(LinearLayout) findViewById(R.id.menu_doa);
        TextView txt_doa=(TextView) findViewById(R.id.txt_doa);
        LinearLayout menu_emergency=(LinearLayout) findViewById(R.id.menu_emergency);
        TextView txt_emergency=(TextView) findViewById(R.id.txt_emergency);
        LinearLayout menu_profile=(LinearLayout) findViewById(R.id.menu_profile);
        TextView txt_profile=(TextView) findViewById(R.id.txt_profile);
        LinearLayout menu_inbox=(LinearLayout) findViewById(R.id.menu_inbox);
        TextView txt_inbox=(TextView) findViewById(R.id.txt_inbox);

        menu_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), profile.class);
                startActivity(i);
            }
        });
        menu_panduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
            }
        });
        menu_doa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Doa.class);
                startActivity(i);
            }
        });
        menu_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), emergency.class);
                startActivity(i);
            }
        });
        menu_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), inbox.class);
                startActivity(i);
            }
        });

        createDatabase();
        //FOOTER
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
        final TextView txt_go=(TextView) findViewById(R.id.txt_go);
        txt_thowaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        txt_sai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), sai.class);
                startActivity(i);
            }
        });
        txt_go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(sai.this, txt_go);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if(id == R.id.pin) {
                            Intent i = new Intent(getApplicationContext(), marker.class);
                            startActivity(i);
                        }

                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

       //circle menu
        C_1 =(TextView) findViewById(R.id.circle1);
        C_2 =(TextView) findViewById(R.id.circle2);
        C_3=(TextView) findViewById(R.id.circle3);
        C_4=(TextView) findViewById(R.id.circle4);
        C_5=(TextView) findViewById(R.id.circle5);
        C_6=(TextView) findViewById(R.id.circle6);
        C_7=(TextView) findViewById(R.id.circle7);
        ImageView img_center =(ImageView) findViewById(R.id.img_center);

        img_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgCenter();
            }
        });

//        Button btnSimple =(Button) findViewById(R.id.buttonSimple);
        C_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("1",C_1);
            }
        });
        C_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("2",C_2);
            }
        });
        C_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("3",C_3);
            }
        });
        C_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("4",C_4);
            }
        });
        C_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("5",C_5);
            }
        });
        C_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("6",C_6);
            }
        });
        C_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("7",C_7);
            }
        });

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        String id = user.get("uid");

        //service
        startService(new Intent(sai.this, BackgroundService.class));

    }

    protected void resetBackground(){
        C_1.setBackgroundResource(R.drawable.circle_sai);
        C_1.setTextColor(Color.BLACK);
        C_2.setBackgroundResource(R.drawable.circle_sai);
        C_2.setTextColor(Color.BLACK);
        C_3.setBackgroundResource(R.drawable.circle_sai);
        C_3.setTextColor(Color.BLACK);
        C_4.setBackgroundResource(R.drawable.circle_sai);
        C_4.setTextColor(Color.BLACK);
        C_5.setBackgroundResource(R.drawable.circle_sai);
        C_5.setTextColor(Color.BLACK);
        C_6.setBackgroundResource(R.drawable.circle_sai);
        C_6.setTextColor(Color.BLACK);
        C_7.setBackgroundResource(R.drawable.circle_sai);
        C_7.setTextColor(Color.BLACK);
    }

    protected void imgCenter(){
        resetBackground();
        Cursor c = database.rawQuery("SELECT * FROM sai WHERE name='sai'", null);
        c.moveToFirst();
        String stat=c.getString(2);
        if(stat.equals("1")){
            insertIntoDB("2",C_2);
        }else if(stat.equals("2")){
            insertIntoDB("3",C_3);
        }else if(stat.equals("3")){
            insertIntoDB("4",C_4);
        }else if(stat.equals("4")){
            insertIntoDB("5",C_5);
        }else if(stat.equals("5")){
            insertIntoDB("6",C_6);
        }else if(stat.equals("6")){
            insertIntoDB("7",C_7);
        }else if(stat.equals("7")){
            insertIntoDB("1",C_1);
        }else{
            insertIntoDB("1",C_1);
        }

    }
    protected void createDatabase(){
        database=openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS sai(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR,status VARCHAR);");
        Cursor mCount= database.rawQuery("select count(*) from sai where name='sai'", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        if(count > 0) {
            String query = "UPDATE sai SET status='0' WHERE name='sai';";
            database.execSQL(query);
        }else {
            String query = "INSERT INTO sai (name,status) VALUES('sai', '0');";
            database.execSQL(query);
        }
    }

    protected void insertIntoDB(String status,TextView circle){
       String query = "UPDATE sai SET status='" + status + "' WHERE name='sai';";
       database.execSQL(query);

        Cursor c = database.rawQuery("SELECT * FROM sai WHERE name='sai'", null);

        c.moveToFirst();
        String stat=c.getString(2);
        circle.setBackgroundResource(R.drawable.circle_hover_sai);
        circle.setTextColor(Color.WHITE);
        Log.d("MyDataShow", "status: " + stat);
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(sai.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
