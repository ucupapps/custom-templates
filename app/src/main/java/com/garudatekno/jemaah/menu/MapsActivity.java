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
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.AppUtils;
import com.garudatekno.jemaah.activity.FetchAddressIntentService;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    private String JSON_STRING,hotel_id, navigasi;

    private TextView mLocationAddress;

    private MarkerOptions markerOptions;

    private Marker marker;

    private AddressResultReceiverHotel mResultReceiver;

    private SQLiteHandler db;
    private SessionManager session;

    String created,email,uid,nama;

    private SQLiteDatabase database;

    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        Intent j = getIntent();
        hotel_id = j.getStringExtra(AppConfig.KEY_ID);
        navigasi = j.getStringExtra(AppConfig.KEY_NAVIGASI);

        mResultReceiver = new AddressResultReceiverHotel(new Handler());

        mLocationAddress = (TextView) findViewById(R.id.Address);

//        session = new SessionManager(getApplicationContext());

        //HEADER
        TextView txt_emergency=(TextView) findViewById(R.id.txt_emergency);
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
        txt_thowaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), thawaf.class);
                startActivity(i);
                finish();
            }
        });
        txt_sai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), sai.class);
                startActivity(i);
                finish();
            }
        });
        txt_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), emergency.class);
                startActivity(i);
                finish();
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
                finish();
            }
        });
        menu_panduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
                finish();
            }
        });
        menu_doa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TitipanDoa.class);
                startActivity(i);
                finish();
            }
        });
        menu_navigasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), navigasi.class);
                startActivity(i);
                finish();
            }
        });
        menu_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), inbox.class);
                startActivity(i);
                finish();
            }
        });

        final ImageView img_home=(ImageView) findViewById(R.id.img_home);
        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
                finish();
            }
        });
        final  ImageView img_setting=(ImageView) findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), setting.class);
                startActivity(i);
                finish();
            }
        });

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        createDatabase();

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        nama = user.get("name");
        email = user.get("email");
        created = user.get("created_at");
        //user
//        getData();
//        editTextuser.setText(uid);

        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/profile.png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Getting reference to btn_find of the layout activity_main
//        Button btn_find = (Button) findViewById(R.id.btn_find);

        // Defining button click event listener for the find button
//        View.OnClickListener findClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Getting reference to EditText to get the user input location
//                EditText etLocation = (EditText) findViewById(R.id.et_location);
//
//                // Getting user input location
//                String location = etLocation.getText().toString();
//
//                if(location!=null && !location.equals("")){
//                    new GeocoderTask().execute(location);
//                }
//            }
//        };

        // Setting button click event listener for the find button
//        btn_find.setOnClickListener(findClickListener);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng) {
                if(marker == null) {
                    markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Set Lokasi");
                    markerOptions.draggable(true);
                    marker = mMap.addMarker(markerOptions);
                    marker.showInfoWindow();
                } else {
                    marker.setPosition(latLng);
                    marker.showInfoWindow();
                }
                LatLng latLngAdd = marker.getPosition();
                Double latAdd = latLngAdd.latitude;
                Double lngAdd = latLngAdd.longitude;
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                temp.setLatitude(latAdd);
                temp.setLongitude(lngAdd);
                startIntentService(temp);
//                Toast.makeText(getApplicationContext(), latLng.toString(), Toast.LENGTH_LONG).show();
            }
        });

        LatLng jakarta = new LatLng(-6.2268682, 106.8289868);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(jakarta));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jakarta, 11.0f));


//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
            onLocationChanged(location);
        }else{
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }

//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mMap.setOnInfoWindowClickListener(this);

//        getJSON();
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @Override
    public void onLocationChanged(Location location) {
        //To clear map data
//        mMap.clear();

        //To hold location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //To create marker in map
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("My Location");
//        //adding marker to the map
//        mMap.addMarker(markerOptions);
//        getJSON();

        //opening position with some zoom level in the map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
        mMap.setOnInfoWindowClickListener(this);
    }

    private void showMarkers(){
        JSONObject jsonObject = null;
        List<Marker> markers = new ArrayList<Marker>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                double lat = jo.getDouble("lat");
                double lng = jo.getDouble("long");

                Marker marker = mMap.addMarker(new MarkerOptions()
                                .title(jo.getString("name"))
                                .position(new LatLng(
                                        lat,lng))
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.graphic_map_icon))
                );
                markers.add(marker);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        ListAdapter adapter = new SimpleAdapter(
//                ViewAllEmployee.this, list, R.layout.list_item,
//                new String[]{Config.TAG_ID,Config.TAG_NAME},
//                new int[]{R.id.id, R.id.name});
//
//        listView.setAdapter(adapter);
    }

    private void setLocation(){
        final LatLng latLng = marker.getPosition();
        final String lat = Double.toString(latLng.latitude);
        final String lng = Double.toString(latLng.longitude);

        class SetLocation extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MapsActivity.this,"Update Data","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
//                JSON_STRING = s;
//                showMarkers();
                try {
                    JSONObject jObj = new JSONObject(s);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONObject hotel = jObj.getJSONObject("data");
                        String lat = hotel.getString("lat");
                        String lng = hotel.getString("long");

                        insertIntoDB("HOTEL",lat,lng);

                        Intent intent = new Intent(MapsActivity.this,
                                navigasi.class);
//                        intent.putExtra(AppConfig.KEY_LAT,lat);
//                        intent.putExtra(AppConfig.KEY_LNG,lng);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
//                        String errorMsg = jObj.getString("error_msg");
                        JSONObject hotel = jObj.getJSONObject("data");
                        String errorMsg = hotel.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_ID, hotel_id);
                data.put(AppConfig.KEY_LAT,lat);
                data.put(AppConfig.KEY_LNG,lng);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(AppConfig.URL_SET_LOCATION, data);
                return res;
            }
        }
        SetLocation gj = new SetLocation();
        gj.execute();
    }

    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            mMap.clear();

            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){

                Address address = (Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);

                mMap.addMarker(markerOptions);

                // Locate the first location
                if(i==0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Bundle args = new Bundle();
        args.putParcelable("longLat_dataPrivider", marker.getPosition());
        args.putString("address", marker.getTitle());

        String lat_nav = Double.toString(marker.getPosition().latitude);
        String lng_nav = Double.toString(marker.getPosition().longitude);

//        Intent i = new Intent(this, SearchActivity.class);
//        i.putExtras(args);
//        startActivity(i);
        if(navigasi.equals("HOTEL")) {
            setLocation();
        }else{
            insertIntoDB(navigasi,lat_nav,lng_nav);
            Intent intent = new Intent(MapsActivity.this,
                    navigasi.class);
            startActivity(intent);
        }
//        Toast.makeText(this, "Info window clicked",
//                Toast.LENGTH_SHORT).show();
    }

    protected void createDatabase(){
        database=openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS locations(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR,lat VARCHAR,lng VARCHAR);");
    }

    protected void insertIntoDB(String name,String lat,String lng){

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

    private void logoutUser() {
        if (session.isLoggedIn()) {
            session.setLogin(false);

            db.deleteUsers();
        }

        // Launching the login activity
        Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

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

    class AddressResultReceiverHotel extends ResultReceiver {
        public AddressResultReceiverHotel(Handler handler) {
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
}