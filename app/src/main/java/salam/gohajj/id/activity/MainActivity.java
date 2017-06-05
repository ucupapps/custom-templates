package salam.gohajj.id.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import salam.gohajj.id.R;
import salam.gohajj.id.helper.SQLiteHandler;
import salam.gohajj.id.helper.SessionManager;
import salam.gohajj.id.menu.profile;
import salam.gohajj.id.menu.sai;
import salam.gohajj.id.menu.TitipanDoa;
import salam.gohajj.id.menu.emergency;
import salam.gohajj.id.menu.inbox;
import salam.gohajj.id.menu.navigasi;
import salam.gohajj.id.menu.panduan;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private SQLiteHandler db;
    private SessionManager session;
    protected GoogleApiClient mGoogleApiClient;
    //list
    private ListView listView;
    private String JSON_STRING;

    protected Location mCurrentLocation;
    protected TextView C_1,C_2,C_3,C_4,C_5,C_6,C_7;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDatabase();
        //HEADER
        TextView txt_emergency=(TextView) findViewById(R.id.txt_emergency);
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
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
        txt_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), emergency.class);
                startActivity(i);
            }
        });

        // FOOTER
        LinearLayout menu_panduan=(LinearLayout) findViewById(R.id.menu_panduan);
        TextView txt_panduan=(TextView) findViewById(R.id.txt_panduan);
        LinearLayout menu_doa=(LinearLayout) findViewById(R.id.menu_doa);
        TextView txt_doa=(TextView) findViewById(R.id.txt_doa);
        LinearLayout menu_navigasi=(LinearLayout) findViewById(R.id.menu_navigasi);
        TextView txt_navigasi=(TextView) findViewById(R.id.txt_emergency);
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
                Intent i = new Intent(getApplicationContext(), TitipanDoa.class);
                startActivity(i);
            }
        });
        menu_navigasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), navigasi.class);
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

        final ImageView img_home=(ImageView) findViewById(R.id.img_home);
        final  ImageView img_setting=(ImageView) findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, img_setting);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.logout) {
                            logoutUser();
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
        ImageView img_center =(ImageView) findViewById(R.id.img_center_thawaf);

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
        String name = user.get("name");
        String email = user.get("email");
        String image = user.get("image");

        //service
        startService(new Intent(MainActivity.this, BackgroundService.class));

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
//
//        imgProfile = (CircleImageView) header.findViewById(R.id.imageProfile);
//        txtEmail = (TextView) header.findViewById(R.id.email);
//        txtName = (TextView) header.findViewById(R.id.name);
//        txtNama = (TextView) findViewById(R.id.username);
        // Displaying the user details on the screen
//        txtNama.setText("Welcome "+ name);


//        Picasso.with(this)
//                    .load("http://192.168.43.31/uploads/profile/"+id+"/images.jpg")
//                    .into(imgProfile);
//        txtName.setText(name);
//        txtEmail.setText(email);
//        imgOrder = (ImageView) findViewById(R.id.img_order);
//        imgOrder.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
                // Menampilkan screen berita
//                Intent i = new Intent(getApplicationContext(), OrderList.class);
//                startActivity(i);
//            }
//        });
//        imgHistory = (ImageView) findViewById(R.id.img_history);
//        imgHistory.setOnClickListener(new View.OnClickListener() {

//            @Override
//            public void onClick(View view) {
                // Menampilkan screen berita
//                Intent i = new Intent(getApplicationContext(), OrderHistory.class);
//                startActivity(i);
//            }
//        });
        //lat lang
//        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
//        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);

        // Create an instance of GoogleAPIClient.
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
    }

//    @Override
//    public void onConnected(Bundle connectionHint) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//           return;
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//             return;
//        }
//        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }

    protected void resetBackground(){
        C_1.setBackgroundResource(R.drawable.circle);
        C_1.setTextColor(Color.BLACK);
        C_2.setBackgroundResource(R.drawable.circle);
        C_2.setTextColor(Color.BLACK);
        C_3.setBackgroundResource(R.drawable.circle);
        C_3.setTextColor(Color.BLACK);
        C_4.setBackgroundResource(R.drawable.circle);
        C_4.setTextColor(Color.BLACK);
        C_5.setBackgroundResource(R.drawable.circle);
        C_5.setTextColor(Color.BLACK);
        C_6.setBackgroundResource(R.drawable.circle);
        C_6.setTextColor(Color.BLACK);
        C_7.setBackgroundResource(R.drawable.circle);
        C_7.setTextColor(Color.BLACK);
    }

    protected void imgCenter(){
        resetBackground();
        Cursor c = database.rawQuery("SELECT * FROM thowaf WHERE name='thawaf'", null);
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
        database.execSQL("CREATE TABLE IF NOT EXISTS thowaf(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR,status VARCHAR);");
        Cursor mCount= database.rawQuery("select count(*) from thowaf where name='thawaf'", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        if(count > 0) {
            String query = "UPDATE thowaf SET status='0' WHERE name='thawaf';";
            database.execSQL(query);
        }else {
            String query = "INSERT INTO thowaf (name,status) VALUES('thawaf', '0');";
            database.execSQL(query);
        }
    }

    protected void insertIntoDB(String status,TextView circle){
       String query = "UPDATE thowaf SET status='" + status + "' WHERE name='thawaf';";
       database.execSQL(query);

        Cursor c = database.rawQuery("SELECT * FROM thowaf WHERE name='thawaf'", null);

        c.moveToFirst();
        String stat=c.getString(2);
        circle.setBackgroundResource(R.drawable.circle_hover);
        circle.setTextColor(Color.WHITE);
        Log.d("MyDataShow", "status: " + stat);
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if(id == R.id.nav_findme) {
//            Intent i = new Intent(getApplicationContext(), AndroidGPSTrackingActivity.class);
//            startActivity(i);
//        }
//        if(id == R.id.nav_order) {
//            Intent i = new Intent(getApplicationContext(), input.class);
//            startActivity(i);
//        }
//        if (id == R.id.nav_logout) {
//            logoutUser();
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//    }

}
