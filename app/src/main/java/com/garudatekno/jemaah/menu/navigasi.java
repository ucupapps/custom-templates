package com.garudatekno.jemaah.menu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;

public class navigasi extends AppCompatActivity implements OnClickListener, OnMapReadyCallback {
    private EditText editTextuser, txtMessage, txtphone, txtlng, txtlat;
    private TextView txtbus, txthotel, txtbertemu, txtmasjid,txtpoi;
    private ImageView imgbus,imghotel,imgpintu,imgbertemu;
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
        setContentView(R.layout.navigasi);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

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


        //HEADER
        TextView txt_emergency=(TextView) findViewById(R.id.txt_emergency);
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);

        txt_thowaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), thawaf.class);
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

        ImageView img = (ImageView) findViewById(R.id.img_navigasi);
        img.setBackgroundResource(R.drawable.circle_green_active);
        img.setPadding(22,22,22,22);
        img.setImageDrawable(getResources().getDrawable(R.drawable.navigasi_active));

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
        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
            }
        });
        final  ImageView img_setting=(ImageView) findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(navigasi.this, img_setting);
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

        editTextuser = (EditText) findViewById(R.id.userid);
        txtMessage = (EditText) findViewById(R.id.pesan);
        txtlat = (EditText) findViewById(R.id.lat);
        txtlng = (EditText) findViewById(R.id.lng);
        editTextuser.setVisibility(View.GONE);
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

        imgbus=(ImageView) findViewById(R.id.arrow_bus);
        imghotel=(ImageView) findViewById(R.id.arrow_hotel);
        imgpintu=(ImageView) findViewById(R.id.arrow_pintu);
        imgbertemu=(ImageView) findViewById(R.id.arrow_bertemu);
        imgbertemu.setOnClickListener(this);
        imgpintu.setOnClickListener(this);
        imgbus.setOnClickListener(this);
        imghotel.setOnClickListener(this);

        txtbus = (TextView) findViewById(R.id.txtbus);
        txthotel = (TextView) findViewById(R.id.txthotel);
        txtbertemu = (TextView) findViewById(R.id.txtbertemu);
        txtmasjid = (TextView) findViewById(R.id.txtmasjid);
        txtpoi = (TextView) findViewById(R.id.txtpoi);
        txtbus.setOnClickListener(this);
        txthotel.setOnClickListener(this);
        txtbertemu.setOnClickListener(this);
        txtmasjid.setOnClickListener(this);
        txtpoi.setOnClickListener(this);

        //cek
        cekData("BUS",txtbus);
        cekData("HOTEL",txthotel);
        cekData("NO PINTU MASJID",txtmasjid);
        cekData("TEMPAT BERTEMU",txtbertemu);

        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/profile.png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }
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
//                public boolean onMarkerClick(Marker navigasi) {
//                    LatLng position = navigasi.getPosition();
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
        Intent intent = new Intent(navigasi.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClick(View v) {
        if (v == txtbus) {
            getCurrentLocation();
            if(txtbus.getText().toString().equals("Set Lokasi")) {
                if (txtlat.getText().toString().equals("")) {
                    Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
                }
                txtMessage.setText("BUS"); insertIntoDB();
                txtbus.setBackgroundResource(R.drawable.button);
                txtbus.setText("Arahkan");
            }else if(txtbus.getText().toString().equals("Arahkan")) {
                Intent intent = new Intent(getApplicationContext(), go.class);
                intent.putExtra(AppConfig.KEY_NAME,"BUS");
                startActivity(intent);
            }
        }
        if (v == txthotel) {
            getCurrentLocation();
            if(txthotel.getText().toString().equals("Set Lokasi")){
                if(txtlat.getText().toString().equals(""))
                {
                    Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
                }
                txtMessage.setText("HOTEL");insertIntoDB();
                txthotel.setBackgroundResource(R.drawable.button);
                txthotel.setText("Arahkan");
            }else if(txthotel.getText().toString().equals("Arahkan")) {
                Intent intent = new Intent(getApplicationContext(), go.class);
                intent.putExtra(AppConfig.KEY_NAME,"HOTEL");
                startActivity(intent);
            }
        }
        if (v == txtmasjid) {
            getCurrentLocation();
            if(txtmasjid.getText().toString().equals("Set Lokasi")){
            if(txtlat.getText().toString().equals(""))
            {
                Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
            }
            txtMessage.setText("NO PINTU MASJID");insertIntoDB();
                txtmasjid.setBackgroundResource(R.drawable.button);
                txtmasjid.setText("Arahkan");
            }else if(txtmasjid.getText().toString().equals("Arahkan")) {
                Intent intent = new Intent(getApplicationContext(), go.class);
                intent.putExtra(AppConfig.KEY_NAME,"NO PINTU MASJID");
                startActivity(intent);
            }
        }
        if (v == txtbertemu) {
            getCurrentLocation();
            if(txtbertemu.getText().toString().equals("Set Lokasi")){
                if(txtlat.getText().toString().equals(""))
                {
                    Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
                }
                txtMessage.setText("TEMPAT BERTEMU");insertIntoDB();
                txtbertemu.setBackgroundResource(R.drawable.button);
                txtbertemu.setText("Arahkan");
            }else if(txtbertemu.getText().toString().equals("Arahkan")) {
                Intent intent = new Intent(getApplicationContext(), go.class);
                intent.putExtra(AppConfig.KEY_NAME,"TEMPAT BERTEMU");
                startActivity(intent);
            }
        }

        if (v == txtpoi) {
            Intent intent = new Intent(getApplicationContext(), Poi.class);
            startActivity(intent);
        }

        //arrow
        if (v == imghotel) {
            if(txthotel.getText().toString().equals("Arahkan")){
                txthotel.setBackgroundResource(R.drawable.button_red);
                txthotel.setText("Set Lokasi");
            }
        }

        if (v == imgbus) {
            if(txtbus.getText().toString().equals("Arahkan")){
                txtbus.setBackgroundResource(R.drawable.button_red);
                txtbus.setText("Set Lokasi");
            }
        }

        if (v == imgpintu) {
            if(txtmasjid.getText().toString().equals("Arahkan")){
                txtmasjid.setBackgroundResource(R.drawable.button_red);
                txtmasjid.setText("Set Lokasi");
            }
        }

        if (v == imgbertemu) {
            if(txtbertemu.getText().toString().equals("Arahkan")){
                txtbertemu.setBackgroundResource(R.drawable.button_red);
                txtbertemu.setText("Set Lokasi");
            }
        }
    }

    protected void cekData(String name,TextView tv){
        Cursor mCount= database.rawQuery("select count(*) from locations where name='" + name + "'", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        if(count > 0) {
            tv.setBackgroundResource(R.drawable.button);
            tv.setText("Arahkan");
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
