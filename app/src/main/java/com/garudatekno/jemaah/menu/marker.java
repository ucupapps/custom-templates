package com.garudatekno.jemaah.menu;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.MainActivity;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import static java.sql.Types.NULL;

public class marker extends AppCompatActivity implements OnClickListener, OnMapReadyCallback {
    private EditText editTextuser, txtMessage, txtphone, txtlng, txtlat;
    private Button btnbus, btnhotel, btnmeeting, btnpintu;
    private Bitmap bitmap;
    private RadioGroup rg;
    private static final int PICK_Camera_IMAGE = 2;
    Uri imageUri;
    String pesan, phone, uid;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    //user
    private SQLiteHandler db;
    private SessionManager session;
    private GoogleMap mMap;
    Location location;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker);
        //enable GPS
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }else{
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }


        LinearLayout menu_panduan = (LinearLayout) findViewById(R.id.menu_panduan);
        TextView txt_panduan = (TextView) findViewById(R.id.txt_panduan);
        LinearLayout menu_doa = (LinearLayout) findViewById(R.id.menu_doa);
        TextView txt_doa = (TextView) findViewById(R.id.txt_doa);
        LinearLayout menu_emergency = (LinearLayout) findViewById(R.id.menu_emergency);
        TextView txt_emergency = (TextView) findViewById(R.id.txt_emergency);
        LinearLayout menu_profile = (LinearLayout) findViewById(R.id.menu_profile);
        TextView txt_profile = (TextView) findViewById(R.id.txt_profile);
        LinearLayout menu_inbox = (LinearLayout) findViewById(R.id.menu_inbox);
        TextView txt_inbox = (TextView) findViewById(R.id.txt_inbox);
//        txt_emergency.setTextColor(getResources().getColor(R.color.colorTextActive));
//        ImageView img_doa = (ImageView) findViewById(R.id.img_emergency);
//        img_doa.setImageDrawable(getResources().getDrawable(R.drawable.emergency_active));
        menu_profile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), profile.class);
                startActivity(i);
            }
        });
        menu_doa.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Doa.class);
                startActivity(i);
            }
        });
        menu_panduan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
            }
        });
        menu_inbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), inbox.class);
                startActivity(i);
            }
        });
        menu_emergency.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), emergency.class);
                startActivity(i);
            }
        });

        //FOOTER
        TextView txt_thowaf = (TextView) findViewById(R.id.txt_thowaf);
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
                PopupMenu popup = new PopupMenu(marker.this, txt_go);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.bus) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"BUS");
                            startActivity(i);
                        }
                        if(id == R.id.hotel) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"HOTEL");
                            startActivity(i);
                        }
                        if(id == R.id.pintu) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"NO PINTU MASJID");
                            startActivity(i);
                        }
                        if(id == R.id.meeting) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"MEETING POINT");
                            startActivity(i);
                        }
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


        editTextuser = (EditText) findViewById(R.id.userid);
        txtMessage = (EditText) findViewById(R.id.pesan);
        txtlat = (EditText) findViewById(R.id.lat);
        txtlng = (EditText) findViewById(R.id.lng);
        editTextuser.setVisibility(View.GONE);
        txtMessage.setVisibility(View.GONE);
        txtlat.setVisibility(View.GONE);
        txtlng.setVisibility(View.GONE);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        createDatabase();

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        phone = user.get("family_phone");
        //user
        editTextuser.setText(uid);
//        txtphone.setText(phone);

        btnbus = (Button) findViewById(R.id.btnbus);
        btnhotel = (Button) findViewById(R.id.btnhotel);
        btnmeeting = (Button) findViewById(R.id.btnmeeting);
        btnpintu = (Button) findViewById(R.id.btnpintu);
        btnbus.setOnClickListener(this);
        btnhotel.setOnClickListener(this);
        btnmeeting.setOnClickListener(this);
        btnpintu.setOnClickListener(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // get the last know location from your location manager.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.d("Mylat", "lat: " + location);
        Double lat;
        Double lng;
//        Double lat= -6.2268682;
//        Double lng= 106.8289868;
        if (location != null) {
            lat=location.getLatitude(); lng=location.getLongitude();
            LatLng jakarta = new LatLng(lat,lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(jakarta));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jakarta, 17));
        }else{
            Toast.makeText(getApplicationContext(),"Location : "+location, Toast.LENGTH_LONG).show();
        }
        mMap.setMyLocationEnabled(true);
//        if (mMap != null) {
//            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//
//                @Override
//                public void onMyLocationChange(Location arg0) {
//                    // TODO Auto-generated method stub
//
//                    mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
//                }
//            });

//            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker) {
//                    LatLng position = marker.getPosition();
//                    Double latitude = position.latitude;
//                    Double longitude = position.longitude;
//                    txtlat.setText("" + latitude);
//                    txtlng.setText("" + longitude);
//                    Toast.makeText(
//                            go.this,
//                            "Lat " + position.latitude + " "
//                                    + "Long " + position.longitude,
//                            Toast.LENGTH_LONG).show();
//                    return true;
//                }
//            });
//        }

    }

    private void getCurrentLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // get the last know location from your location manager.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
           return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        txtlat.setText("" + location.getLatitude());
        txtlng.setText("" + location.getLongitude());
//        Toast.makeText(
//                go.this,
//                "Lat " + location.getLatitude() + " "
//                        + "Long " + location.getLongitude(),
//                Toast.LENGTH_LONG).show();
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(marker.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClick(View v) {
        if (v == btnbus) {
            getCurrentLocation();
            if(txtlat.getText().toString().equals(""))
            {
                Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
            }
            txtMessage.setText("BUS");insertIntoDB();
        }
        if (v == btnhotel) {
            getCurrentLocation();
            if(txtlat.getText().toString().equals(""))
            {
                Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
            }
            txtMessage.setText("HOTEL");insertIntoDB();
        }
        if (v == btnpintu) {
            getCurrentLocation();
            if(txtlat.getText().toString().equals(""))
            {
                Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
            }
            txtMessage.setText("NO PINTU MASJID");insertIntoDB();
        }
        if (v == btnmeeting) {
            getCurrentLocation();
            if(txtlat.getText().toString().equals(""))
            {
                Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
            }
            txtMessage.setText("MEETING POINT");insertIntoDB();
        }
    }

    protected void createDatabase(){
        database=openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS locations(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR,lat VARCHAR,lng VARCHAR);");
    }

    protected void insertIntoDB(){
        final String idUser = editTextuser.getText().toString().trim();
        final String lat = txtlat.getText().toString().trim();
        final String lng = txtlng.getText().toString().trim();
        final String name = txtMessage.getText().toString().trim();

        Cursor mCount= database.rawQuery("select count(*) from locations where name='" + name + "'", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        if(count > 0) {
            String query = "UPDATE locations SET lat='" + lat + "',lng='" + lng + "' WHERE name='" + name + "';";
            database.execSQL(query);
        }else {
            String query = "INSERT INTO locations (name,lat,lng) VALUES('" + name + "', '" + lat + "', '" + lng + "');";
            database.execSQL(query);
        }
        Toast.makeText(getApplicationContext(),"Location "+name+ " Berhasil di simpan", Toast.LENGTH_LONG).show();
        Cursor c = database.rawQuery("SELECT * FROM locations WHERE name='" + name + "'", null);

        c.moveToFirst();
        String nama=c.getString(1);
        String lats=c.getString(2);
        String lngs=c.getString(3);
        Log.d("MyDataShow", "Name: " + nama+"Lat: " + lats+"Lng: " + lngs);
    }

}
