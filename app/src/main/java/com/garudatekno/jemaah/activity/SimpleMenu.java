package com.garudatekno.jemaah.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.garudatekno.jemaah.menu.Doa;
import com.garudatekno.jemaah.menu.emergency;
import com.garudatekno.jemaah.menu.profile;
import com.garudatekno.jemaah.sample.ViewData;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;

public class SimpleMenu extends AppCompatActivity
        implements ListView.OnItemClickListener {

    private SQLiteHandler db;
    private SessionManager session;
    protected GoogleApiClient mGoogleApiClient;
    //list
    private ListView listView;
    private String JSON_STRING;

    protected Location mCurrentLocation;

    // UI Widgets.
    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_menu);
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

        //FOOTER
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_go=(TextView) findViewById(R.id.txt_go);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
        txt_thowaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SimpleMenu.class);
                startActivity(i);
            }
        });

        Button btnSimple =(Button) findViewById(R.id.buttonSimple);
        btnSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        String id = user.get("uid");
        String name = user.get("name");
        String email = user.get("email");
        String image = user.get("image");

        //service
        startService(new Intent(SimpleMenu.this, BackgroundService.class));

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

    protected void onStart() {
//        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
//        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(SimpleMenu.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

//    @Override
//        public void onBackPressed() {
//            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//            if (drawer.isDrawerOpen(GravityCompat.START)) {
//                drawer.closeDrawer(GravityCompat.START);
//            } else {
//                super.onBackPressed();
//            }
//    }

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ViewData.class);
        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        String empId = map.get(AppConfig.TAG_ID).toString();
        intent.putExtra(AppConfig.EMP_ID,empId);
        startActivity(intent);
    }

}
