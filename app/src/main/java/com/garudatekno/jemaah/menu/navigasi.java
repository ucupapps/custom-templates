package com.garudatekno.jemaah.menu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.AppUtils;
import com.garudatekno.jemaah.activity.FetchAddressIntentService;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.app.AppController;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.readystatesoftware.viewbadger.BadgeView;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;

public class navigasi extends AppCompatActivity implements OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private EditText editTextuser, txtMessage, txtphone, txtlng, txtlat;
    private TextView txtbus, txthotel, txtbertemu, txtmasjid,txtpoi;
    private ImageView imgbus,imghotel,imgpintu,imgbertemu,imgPoi;
    private RadioGroup rg;
    private static final int PICK_Camera_IMAGE = 2;
    Uri imageUri;
    String pesan, phone, uid, txtlatHotel, txtlngHotel;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    //user
    private SQLiteHandler db;
    private SessionManager session;
    private GoogleMap mMap;
    Location location;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    Context mContext;
    TextView mLocationMarkerText,txtsetLocation;
    private LatLng mCenterLatLong;
    private GoogleApiClient mGoogleApiClient;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    /**
     * The formatted location address.
     */
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    TextView mLocationText,mLocationAddress;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private SQLiteDatabase database;

    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            /* ETC.. */
    };

    private Tracker mTracker;
    View target ;
    BadgeView badge ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigasi);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        final TextView txtkoneksi= (TextView) findViewById(R.id.txtkoneksi);
        Thread th = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!cek_status(getApplicationContext()))
                                {
                                    txtkoneksi.setVisibility(View.VISIBLE);
                                }else{
                                    txtkoneksi.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        th.start();
        //tracker
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        sendScreenImageName("Navigasi");
        //badge
        target = findViewById(R.id.img_inbox);
        badge = new BadgeView(this, target);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        ShortcutBadger.removeCount(getApplicationContext());
        badge.hide();
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            if (cek_status(getApplicationContext()))
            {
                CountInbox();
            }
        }

        mLocationAddress = (TextView) findViewById(R.id.Address);
        mLocationMarkerText = (TextView) findViewById(R.id.locationMarkertext);
        mLocationText = (TextView) findViewById(R.id.Locality);
        txtsetLocation = (TextView) findViewById(R.id.setLocation);

        Intent latLong = getIntent();
        txtlatHotel = latLong.getStringExtra("lat");
        txtlngHotel = latLong.getStringExtra("lng");

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
        buildGoogleApiClient();
        mResultReceiver = new AddressResultReceiver(new Handler());

        txtsetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lokasi=mLocationMarkerText.getText().toString().trim();
                Toast.makeText(getApplicationContext(),lokasi, Toast.LENGTH_LONG).show();

            }


        });

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
                Intent i = new Intent(getApplicationContext(), setting.class);
                startActivity(i);
            }
        });
        editTextuser = (EditText) findViewById(R.id.userid);
        txtMessage = (EditText) findViewById(R.id.pesan);
        txtlat = (EditText) findViewById(R.id.lat);
        txtlng = (EditText) findViewById(R.id.lng);
        editTextuser.setVisibility(View.GONE);

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        createDatabase();
        createDatabaseMasjid();

        phone = user.get("family_phone");
        //user
        editTextuser.setText(uid);
//        txtphone.setText(phone);

        imgbus=(ImageView) findViewById(R.id.arrow_bus);
        imghotel=(ImageView) findViewById(R.id.arrow_hotel);
        imgpintu=(ImageView) findViewById(R.id.arrow_pintu);
        imgbertemu=(ImageView) findViewById(R.id.arrow_bertemu);
        imgPoi=(ImageView) findViewById(R.id.arrow_poi);

        imgbertemu.setOnClickListener(this);
        imgpintu.setOnClickListener(this);
        imgbus.setOnClickListener(this);
        imghotel.setOnClickListener(this);
        imgPoi.setOnClickListener(this);

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
        cekDataMasjid(uid,txtmasjid);
        cekData("TEMPAT BERTEMU",txtbertemu);
        cekData("POI",txtpoi);

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

    public boolean cek_status(Context cek) {

        ConnectivityManager cm = (ConnectivityManager) cek.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected())
        {
            return true;
        } else{
            return false;
        }
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        // get the last know location from your location manager.
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            return;
//        }
//        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        Log.d("Mylat", "lat: " + location);
//        Double lat;
//        Double lng;
////        Double lat= -6.2268682;
////        Double lng= 106.8289868;
//        if (location != null) {
//            lat=location.getLatitude(); lng=location.getLongitude();
//            LatLng jakarta = new LatLng(lat,lng);
//
////            CameraPosition INIT =
////                    new CameraPosition.Builder()
////                            .target(jakarta)
////                            .zoom(17.5F)
////                            .bearing(300F) // orientation
////                            .tilt( 50F) // viewing angle
////                            .build();
////
////            // use map to move camera into position
////            mMap.moveCamera( CameraUpdateFactory.newCameraPosition(INIT) );
//
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(jakarta));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jakarta, 17));
//
//            //create initial marker
//            mMap.addMarker( new MarkerOptions()
//                    .position(jakarta)
//                    .title("Location")
//                    .snippet("First Marker").draggable(true)).showInfoWindow();
//
//        }else{
//            Toast.makeText(getApplicationContext(),"Location : "+location, Toast.LENGTH_LONG).show();
//        }
//        mMap.setMyLocationEnabled(true);
////        if (mMap != null) {
////            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
////
////                @Override
////                public void onMyLocationChange(Location arg0) {
////                    // TODO Auto-generated method stub
////
////                    mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
////                }
////            });
//
////            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
////                @Override
////                public boolean onMarkerClick(Marker navigasi) {
////                    LatLng position = navigasi.getPosition();
////                    Double latitude = position.latitude;
////                    Double longitude = position.longitude;
////                    txtlat.setText("" + latitude);
////                    txtlng.setText("" + longitude);
////                    Toast.makeText(
////                            go.this,
////                            "Lat " + position.latitude + " "
////                                    + "Long " + position.longitude,
////                            Toast.LENGTH_LONG).show();
////                    return true;
////                }
////            });
////        }
//
//    }

    private void sendScreenImageName(String name) {
        // [START screen_view_hit]
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
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
        if (session.isLoggedIn()) {
            session.setLogin(false);
            db.deleteUsers();
        }

        // Launching the login activity
        Intent intent = new Intent(navigasi.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClick(View v) {
        if (v == txtbus) {
            if(txtbus.getText().toString().equals("Set Lokasi")) {
                if (txtlat.getText().toString().equals("")) {
                    Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
                }
                Intent intentBus = new Intent(getApplicationContext(), MapsActivity.class);
                intentBus.putExtra(AppConfig.KEY_NAVIGASI,"BUS");
                startActivity(intentBus);

                txtMessage.setText("BUS");
                cekData("BUS",txtbus);
//                insertIntoDB();
//                txtbus.setBackgroundResource(R.drawable.button);
//                txtbus.setText("Arahkan");
            }else if(txtbus.getText().toString().equals("Arahkan")) {
                Intent intent = new Intent(getApplicationContext(), go.class);
                intent.putExtra(AppConfig.KEY_NAME,"BUS");
                startActivity(intent);
            }
        }
        if (v == txthotel) {
            if(txthotel.getText().toString().equals("Set Lokasi")){
                if(txtlat.getText().toString().equals(""))
                {
                    Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getApplicationContext(), Hotel.class);
                startActivity(intent);

                Log.e("latLong : ", txtlatHotel+","+txtlngHotel);

                txtMessage.setText("HOTEL");
                cekData("HOTEL",txthotel);
//                txthotel.setBackgroundResource(R.drawable.button);
//                txthotel.setText("Arahkan");
            }else if(txthotel.getText().toString().equals("Arahkan")) {
//                insertIntoDB();
                Intent intent = new Intent(getApplicationContext(), go.class);
                intent.putExtra(AppConfig.KEY_NAME,"HOTEL");
                startActivity(intent);
            }
        }
        if (v == txtmasjid) {
            if(txtmasjid.getText().toString().equals("Set Lokasi")){
            if(txtlat.getText().toString().equals(""))
            {
                Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
            }
                Intent intentPintu = new Intent(getApplicationContext(), PintuMasjid.class);
                startActivity(intentPintu);
                txtMessage.setText("NO PINTU MASJID");
//                insertIntoDB();
//                txtmasjid.setBackgroundResource(R.drawable.button);
//                txtmasjid.setText("Lihat");
                cekDataMasjid(uid,txtmasjid);
            }else if(txtmasjid.getText().toString().equals("Lihat")) {
                Intent intent = new Intent(getApplicationContext(), LihatPintuMasjid.class);
                intent.putExtra(AppConfig.KEY_NAME,"NO PINTU MASJID");
                startActivity(intent);
            }
        }
        if (v == txtbertemu) {
            if(txtbertemu.getText().toString().equals("Set Lokasi")){
                if(txtlat.getText().toString().equals(""))
                {
                    Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
                }

                Intent intentBertemu = new Intent(getApplicationContext(), MapsActivity.class);
                intentBertemu.putExtra(AppConfig.KEY_NAVIGASI,"TEMPAT BERTEMU");
                startActivity(intentBertemu);
                txtMessage.setText("TEMPAT BERTEMU");
                cekData("TEMPAT BERTEMU",txtbertemu);
//                insertIntoDB();
//                txtbertemu.setBackgroundResource(R.drawable.button);
//                txtbertemu.setText("Arahkan");
            }else if(txtbertemu.getText().toString().equals("Arahkan")) {
                Intent intent = new Intent(getApplicationContext(), go.class);
                intent.putExtra(AppConfig.KEY_NAME,"TEMPAT BERTEMU");
                startActivity(intent);
            }
        }

        if (v == txtpoi) {
            if(txtpoi.getText().toString().equals("Set Lokasi")){
                if(txtlat.getText().toString().equals(""))
                {
                    Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getApplicationContext(), Poi.class);
                startActivity(intent);

//                Log.e("latLong : ", txtlatHotel+","+txtlngHotel);

                txtMessage.setText("POI");
//                txthotel.setBackgroundResource(R.drawable.button);
//                txthotel.setText("Arahkan");
                cekData("POI",txtpoi);
            }else if(txtpoi.getText().toString().equals("Arahkan")) {
                Intent intent = new Intent(getApplicationContext(), go.class);
                intent.putExtra(AppConfig.KEY_NAME,"POI");
                startActivity(intent);
            }
        }

//        if (v == txtpoi) {
//            Intent intent = new Intent(getApplicationContext(), Poi.class);
//            startActivity(intent);
//        }

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
            if(txtmasjid.getText().toString().equals("Lihat")){
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

        if (v == imgPoi) {
            if(txtpoi.getText().toString().equals("Arahkan")){
                txtpoi.setBackgroundResource(R.drawable.button_red);
                txtpoi.setText("Set Lokasi");
            }
        }
    }

    protected void cekData(String name,TextView tv){
        Cursor mCount= database.rawQuery("select count(*) from locations where name='" + name + "'", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        if(count > 0) {
            tv.setBackgroundResource(R.drawable.button);
            tv.setPadding(5,5,5,5);
            if(name.equals("NO PINTU MASJID")){
                tv.setText("Lihat");
            }else {
                tv.setText("Arahkan");
            }
        }else{
            tv.setBackgroundResource(R.drawable.button_red);
            tv.setPadding(5,5,5,5);
            tv.setText("Set Lokasi");
        }
    }

    protected void cekDataMasjid(String user_id,TextView tm){
        Cursor mCount= database.rawQuery("select count(*) from pintu_masjid where user_id='" + user_id + "'", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        if(count > 0) {
            tm.setBackgroundResource(R.drawable.button);
            tm.setPadding(5,5,5,5);
            tm.setText("Lihat");
        }else{
            tm.setBackgroundResource(R.drawable.button_red);
            tm.setPadding(5,5,5,5);
            tm.setText("Set Lokasi");
        }
    }
    protected void createDatabase(){
        database=openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS locations(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR,lat VARCHAR,lng VARCHAR);");
    }

    protected void createDatabaseMasjid(){
        database=openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS pintu_masjid(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, user_id VARCHAR,no_pintu VARCHAR);");
    }

    protected void insertIntoDB(){
        String lat,lng;
        final String idUser = editTextuser.getText().toString().trim();
        final String name = txtMessage.getText().toString().trim();
        if(name.equals("HOTEL")) {
            lat = txtlatHotel;
            lng = txtlngHotel;
        }else{
            lat = txtlat.getText().toString().trim();
            lng = txtlng.getText().toString().trim();
        }

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean hasPermissions(@NonNull String... permissions) {
        for (String permission : permissions)
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permission))
                return false;
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;
                mMap.clear();

                try {
                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    startIntentService(mLocation);
                    mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);
                    txtlng.setText("" + mCenterLatLong.latitude + "");
                    txtlat.setText("" + mCenterLatLong.longitude+ "");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(            mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(19f).tilt(70).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
            txtlng.setText("" + location.getLatitude() + "");
            txtlat.setText("" + location.getLongitude()+ "");
            startIntentService(location);


        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }


    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));


            }


        }

    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAreaOutput != null)
                // mLocationText.setText(mAreaOutput+ "");

                mLocationAddress.setText(mAddressOutput);
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }


    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);

                // TODO call location based filter


                LatLng latLong;


                latLong = place.getLatLng();

                //mLocationText.setText(place.getName() + "");

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(19f).tilt(70).build();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));


            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }

    protected void CountInbox(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String hsl = s.trim();
                Integer a = Integer.parseInt(hsl);
                if(a > 0){
                    badge.setText(hsl);
                    badge.show();
                    ShortcutBadger.applyCount(getApplicationContext(), a);
                }else {
                    ShortcutBadger.removeCount(getApplicationContext());
                    badge.hide();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(AppConfig.URL_COUNT_INBOX,uid);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

}
