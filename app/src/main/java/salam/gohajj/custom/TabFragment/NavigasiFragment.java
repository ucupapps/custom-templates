package salam.gohajj.custom.TabFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
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

import me.anwarshahriar.calligrapher.Calligrapher;
import salam.gohajj.custom.R;
import salam.gohajj.custom.Utilities;
import salam.gohajj.custom.activity.AppUtils;
import salam.gohajj.custom.activity.FetchAddressIntentService;
import salam.gohajj.custom.activity.LoginActivity;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.app.AppController;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;
import salam.gohajj.custom.menu.Hotel;
import salam.gohajj.custom.menu.LihatPintuMasjid;
import salam.gohajj.custom.menu.MapsActivity;
import salam.gohajj.custom.menu.PintuMasjid;
import salam.gohajj.custom.menu.Poi;
import salam.gohajj.custom.menu.go;
import salam.gohajj.custom.menu.panduan;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;


public class NavigasiFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private EditText editTextuser, txtMessage, txtphone, txtlng, txtlat;
    private TextView txtbus, txthotel, txtbertemu, txtmasjid, txtpoi;
    private ImageView imgbus, imghotel, imgpintu, imgbertemu, imgPoi;
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
    protected LocationManager locationManager;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    Context mContext;
    TextView mLocationMarkerText, txtsetLocation;
    private LatLng mCenterLatLong;
    private LatLng latLng;
    GoogleApiClient mGoogleApiClient;

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
    TextView mLocationText, mLocationAddress;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private SQLiteDatabase database;

    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    static final Integer READ_EXST = 0x4;
    LocationRequest mLocationRequest;
    private Tracker mTracker;
    View target;
    BadgeView badge;
    private String getpref;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public NavigasiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.tab_navigasi, null);
        mContext = container.getContext();

        Calligrapher calligrapher = new Calligrapher(getActivity());
        calligrapher.setFont(getActivity(), "fonts/helvetica.ttf", true);
        final TextView txtkoneksi = (TextView) vi.findViewById(R.id.txtkoneksi);
        if (!Utilities.cek_status(getContext())) {
            txtkoneksi.setVisibility(View.VISIBLE);
        } else {
            txtkoneksi.setVisibility(View.GONE);
        }
        mLocationAddress = (TextView) vi.findViewById(R.id.Address);
        mLocationMarkerText = (TextView) vi.findViewById(R.id.locationMarkertext);
        mLocationText = (TextView) vi.findViewById(R.id.Locality);
        txtsetLocation = (TextView) vi.findViewById(R.id.setLocation);
        txtsetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lokasi = mLocationMarkerText.getText().toString().trim();
                Toast.makeText(getContext(), lokasi, Toast.LENGTH_LONG).show();
            }
        });

        editTextuser = (EditText) vi.findViewById(R.id.userid);
        txtMessage = (EditText) vi.findViewById(R.id.pesan);
        txtlat = (EditText) vi.findViewById(R.id.lat);
        txtlng = (EditText) vi.findViewById(R.id.lng);
        target = vi.findViewById(R.id.img_inbox);
        editTextuser.setVisibility(View.GONE);

        createDatabase();
        createDatabaseMasjid();

        //user
        editTextuser.setText(uid);
//        txtphone.setText(phone);

        imgbus = (ImageView) vi.findViewById(R.id.arrow_bus);
        imghotel = (ImageView) vi.findViewById(R.id.arrow_hotel);
        imgpintu = (ImageView) vi.findViewById(R.id.arrow_pintu);
        imgbertemu = (ImageView) vi.findViewById(R.id.arrow_bertemu);
        imgPoi = (ImageView) vi.findViewById(R.id.arrow_poi);

        imgbertemu.setOnClickListener(this);
        imgpintu.setOnClickListener(this);
        imgbus.setOnClickListener(this);
        imghotel.setOnClickListener(this);
        imgPoi.setOnClickListener(this);

        txtbus = (TextView) vi.findViewById(R.id.txtbus);
        txthotel = (TextView) vi.findViewById(R.id.txthotel);
        txtbertemu = (TextView) vi.findViewById(R.id.txtbertemu);
        txtmasjid = (TextView) vi.findViewById(R.id.txtmasjid);
        txtpoi = (TextView) vi.findViewById(R.id.txtpoi);

        txtbus.setOnClickListener(this);
        txthotel.setOnClickListener(this);
        txtbertemu.setOnClickListener(this);
        txtmasjid.setOnClickListener(this);
        txtpoi.setOnClickListener(this);

        //cek
        cekData("BUS", txtbus);
        cekData("HOTEL", txthotel);
        cekDataMasjid(uid, txtmasjid);
        cekData("TEMPAT BERTEMU", txtbertemu);
        cekData("POI", txtpoi);
        cekMapFragment();
        return vi;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = getActivity().openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        getpref = Utilities.getPref("id_pref", getActivity()) != null ? Utilities.getPref("id_pref", getActivity()) : "";
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        session = new SessionManager(getContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        if (Build.VERSION.SDK_INT > 22 && !hasPermissions(requiredPermissions)) {
            //permission
            askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, READ_EXST);
        }

        //tracker
        AppController application = (AppController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        sendScreenImageName("Navigasi");
        // SqLite database handler
        db = new SQLiteHandler(getContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        phone = user.get("family_phone");

        database = getActivity().openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);

        Intent latLong = getActivity().getIntent();
        txtlatHotel = latLong.getStringExtra("lat");
        txtlngHotel = latLong.getStringExtra("lng");

        //enable GPS
        LocationManager service = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            if (session.isLoggedIn()) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            Utilities.ShowLog(TAG, "tidak aktif");
            cekMapFragment();
        }
        buildGoogleApiClient();
        mResultReceiver = new AddressResultReceiver(new Handler());

    }

    private void cekMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        buildGoogleApiClient();
        mResultReceiver = new AddressResultReceiver(new Handler());
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
//            Toast.makeText(getContext(),"Location : "+location, Toast.LENGTH_LONG).show();
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

//    private void getCurrentLocation(){
//        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
//        // get the last know location from your location manager.
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            return;
//        }
//        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        txtlat.setText("" + location.getLatitude());
//        txtlng.setText("" + location.getLongitude());
////        Toast.makeText(
////                go.this,
////                "Lat " + location.getLatitude() + " "
////                        + "Long " + location.getLongitude(),
////                Toast.LENGTH_LONG).show();
//    }

    private void logoutUser() {
        if (session.isLoggedIn()) {
            session.setLogin(false);
            db.deleteUsers();
        }

        // Launching the login activity
        Intent intent = new Intent(NavigasiFragment.this.getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void onClick(View v) {
        if (v == txtbus) {
            if (txtbus.getText().toString().equals(getResources().getString(R.string.set_lokasi))) {
                if (txtlat.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Current location cannot null !", Toast.LENGTH_SHORT).show();
                }
                Intent intentBus = new Intent(getContext(), MapsActivity.class);
                intentBus.putExtra(AppConfig.KEY_NAVIGASI, "BUS");
                startActivity(intentBus);

                txtMessage.setText("BUS");
//                cekData("BUS",txtbus);
//                insertIntoDB();
//                txtbus.setBackgroundResource(R.drawable.button);
//                txtbus.setText(getResources().getString(R.string.arahkan));
            } else if (txtbus.getText().toString().equals(getResources().getString(R.string.arahkan))) {
                Intent intent = new Intent(getContext(), go.class);
                intent.putExtra(AppConfig.KEY_NAME, "BUS");
                startActivity(intent);
            }
        }
        if (v == txthotel) {
            if (txthotel.getText().toString().equals(getResources().getString(R.string.set_lokasi))) {
                if (txtlat.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Current location cannot null !", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getContext(), Hotel.class);
                startActivity(intent);

                Log.e("latLong : ", txtlatHotel + "," + txtlngHotel);

                txtMessage.setText("HOTEL");
//                cekData("HOTEL",txthotel);
//                txthotel.setBackgroundResource(R.drawable.button);
//                txthotel.setText(getResources().getString(R.string.arahkan));
            } else if (txthotel.getText().toString().equals(getResources().getString(R.string.arahkan))) {
//                insertIntoDB();
                Intent intent = new Intent(getContext(), go.class);
                intent.putExtra(AppConfig.KEY_NAME, "HOTEL");
                startActivity(intent);
            }
        }
        if (v == txtmasjid) {
            if (txtmasjid.getText().toString().equals(getResources().getString(R.string.set_lokasi))) {
                if (txtlat.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Current location cannot null !", Toast.LENGTH_SHORT).show();
                }
                Intent intentPintu = new Intent(getContext(), PintuMasjid.class);
                startActivity(intentPintu);
                txtMessage.setText("NO PINTU MASJID");
//                insertIntoDB();
//                txtmasjid.setBackgroundResource(R.drawable.button);
//                txtmasjid.setText("Lihat");
//                cekDataMasjid(uid,txtmasjid);
            } else if (txtmasjid.getText().toString().equals("Lihat")) {
                Intent intent = new Intent(getContext(), LihatPintuMasjid.class);
                intent.putExtra(AppConfig.KEY_NAME, "NO PINTU MASJID");
                startActivity(intent);
            }
        }
        if (v == txtbertemu) {
            if (txtbertemu.getText().toString().equals(getResources().getString(R.string.set_lokasi))) {
                if (txtlat.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Current location cannot null !", Toast.LENGTH_SHORT).show();
                }

                Intent intentBertemu = new Intent(getContext(), MapsActivity.class);
                intentBertemu.putExtra(AppConfig.KEY_NAVIGASI, "TEMPAT BERTEMU");
                startActivity(intentBertemu);
                getActivity().finish();
                txtMessage.setText("TEMPAT BERTEMU");
//                cekData("TEMPAT BERTEMU",txtbertemu);
//                insertIntoDB();
//                txtbertemu.setBackgroundResource(R.drawable.button);
//                txtbertemu.setText(getResources().getString(R.string.arahkan));
            } else if (txtbertemu.getText().toString().equals(getResources().getString(R.string.arahkan))) {
                Intent intent = new Intent(getContext(), go.class);
                intent.putExtra(AppConfig.KEY_NAME, "TEMPAT BERTEMU");
                startActivity(intent);
            }
        }

        if (v == txtpoi) {
            if (txtpoi.getText().toString().equals(getResources().getString(R.string.set_lokasi))) {
                if (txtlat.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Current location cannot null !", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getContext(), Poi.class);
                startActivity(intent);

//                Log.e("latLong : ", txtlatHotel+","+txtlngHotel);

                txtMessage.setText("POI");
//                txthotel.setBackgroundResource(R.drawable.button);
//                txthotel.setText(getResources().getString(R.string.arahkan));
//                cekData("POI",txtpoi);
            } else if (txtpoi.getText().toString().equals(getResources().getString(R.string.arahkan))) {
                Intent intent = new Intent(getContext(), go.class);
                intent.putExtra(AppConfig.KEY_NAME, "POI");
                startActivity(intent);
            }
        }

//        if (v == txtpoi) {
//            Intent intent = new Intent(getContext(), Poi.class);
//            startActivity(intent);
//        }

        //arrow
        if (v == imghotel) {
            arrowcekData("HOTEL", txthotel);
        }

        if (v == imgbus) {
            arrowcekData("BUS", txtbus);
        }

        if (v == imgpintu) {
            File file = new File("/sdcard/android/data/salam.gohajj.id/images/pintuMasjid.jpg");
            if (file.exists()) {
                if (txtmasjid.getText().toString().equals("Lihat")) {
                    txtmasjid.setBackgroundResource(R.drawable.button_red);
                    txtmasjid.setText(getResources().getString(R.string.set_lokasi));
                } else {
                    txtmasjid.setBackgroundResource(R.drawable.button);
                    txtmasjid.setText("Lihat");
                }
            }
        }

        if (v == imgbertemu) {

            arrowcekData("TEMPAT BERTEMU", txtbertemu);
        }

        if (v == imgPoi) {
            arrowcekData("POI", txtpoi);
        }
    }

    protected void cekData(String name, TextView tv) {
        Cursor mCount = database.rawQuery("select count(*) from locations where name='" + name + "'", null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        if (count > 0) {
            tv.setBackgroundResource(R.drawable.button);
            tv.setPadding(5, 5, 5, 5);
            if (name.equals("NO PINTU MASJID")) {
                tv.setText("Lihat");
            } else {
                tv.setText(getResources().getString(R.string.arahkan));
            }
        } else {
            tv.setBackgroundResource(R.drawable.button_red);
            tv.setPadding(5, 5, 5, 5);
            tv.setText(getResources().getString(R.string.set_lokasi));
        }
    }

    protected void arrowcekData(String name, TextView tv) {

        Cursor mCount = database.rawQuery("select count(*) from locations where name='" + name + "'", null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        if (count > 0) {
            if (tv.getText().toString().equals(getResources().getString(R.string.arahkan)) || tv.getText().toString().equals("Lihat")) {
                tv.setBackgroundResource(R.drawable.button_red);
                tv.setText(getResources().getString(R.string.set_lokasi));
            } else {
                tv.setBackgroundResource(R.drawable.button);
                tv.setText(getResources().getString(R.string.arahkan));
            }
        } else {

        }
    }

    protected void cekDataMasjid(String user_id, TextView tm) {
        Cursor mCount = database.rawQuery("select count(*) from pintu_masjid where user_id='" + user_id + "'", null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        if (count > 0) {
            tm.setBackgroundResource(R.drawable.button);
            tm.setPadding(5, 5, 5, 5);
            tm.setText("Lihat");
        } else {
            tm.setBackgroundResource(R.drawable.button_red);
            tm.setPadding(5, 5, 5, 5);
            tm.setText(getResources().getString(R.string.set_lokasi));
        }
    }

    protected void createDatabase() {
        database = getActivity().openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS locations(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR,lat VARCHAR,lng VARCHAR);");
    }

    protected void createDatabaseMasjid() {
        database = getActivity().openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS pintu_masjid(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, user_id VARCHAR,no_pintu VARCHAR);");
    }

    protected void insertIntoDB() {
        String lat, lng;
        final String idUser = editTextuser.getText().toString().trim();
        final String name = txtMessage.getText().toString().trim();
        if (name.equals("HOTEL")) {
            lat = txtlatHotel;
            lng = txtlngHotel;
        } else {
            lat = txtlat.getText().toString().trim();
            lng = txtlng.getText().toString().trim();
        }

        Cursor mCount = database.rawQuery("select count(*) from locations where name='" + name + "'", null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        if (count > 0) {
            String query = "UPDATE locations SET lat='" + lat + "',lng='" + lng + "' WHERE name='" + name + "';";
            database.execSQL(query);
        } else {
            String query = "INSERT INTO locations (name,lat,lng) VALUES('" + name + "', '" + lat + "', '" + lng + "');";
            database.execSQL(query);
        }
        Toast.makeText(getContext(), "Location " + name + " Berhasil di simpan", Toast.LENGTH_LONG).show();
        Cursor c = database.rawQuery("SELECT * FROM locations WHERE name='" + name + "'", null);

        c.moveToFirst();
        String nama = c.getString(1);
        String lats = c.getString(2);
        String lngs = c.getString(3);
        Log.e("MyDataShow", "Name: " + nama + "Lat: " + lats + "Lng: " + lngs);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "OnMapReady");
        mMap = googleMap;

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.e("Camera postion change" + "", cameraPosition + "");

//                latLng = new LatLng(location.getLatitude(),location.getLongitude());
                Log.e("INI ERROR", ""+latLng);
                mCenterLatLong = cameraPosition.target;
                Utilities.ShowLog("Maps", "" + mMap);
                if (mMap != null) {
//                    mMap.clear();
                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        return;
                    mMap.setMyLocationEnabled(true);
//                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    locationManager = (LocationManager) mContext
                            .getSystemService(LOCATION_SERVICE);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    CameraPosition cameraPosition1 = new CameraPosition.Builder()
                            .target(latLng).zoom(19f).tilt(70).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                }

                try {
                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    startIntentService(mLocation);
                    mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);
                    txtlng.setText("" + mCenterLatLong.latitude + "");
                    txtlat.setText("" + mCenterLatLong.longitude+ "");

                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }

    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

//        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //move map camera
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
//        try {
//            if (location != null)
//                changeMap(location);
//            LocationServices.FusedLocationApi.removeLocationUpdates(
//                    mGoogleApiClient, this);
//
//        } catch (Exception e) {
//        }
//
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        Utilities.ShowLog("conteeext",""+mContext);
//        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//            }
//        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
//            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;
            Utilities.ShowLog("lokasi",""+location.getLatitude());


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
            Utilities.ShowLog("NavigasiFragment","Sorry! unable to create maps");

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
//            e.printStackTrace();
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        Intent intent = new Intent(getContext(), FetchAddressIntentService.class);
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);
        getActivity().startService(intent);
    }


    private void openAutocompleteActivity() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(getActivity());
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
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

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));


            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
        } else if (resultCode == RESULT_CANCELED) {
        }
    }

//    protected void CountInbox(){
//        Cursor c= database.rawQuery("select * from badge where id=1 ", null);
//        c.moveToFirst();
//        int jumlah=c.getInt(1);
//        //target = vi.findViewById(R.id.img_inbox);
//        badge = new BadgeView(getContext(), target);
//        //badge
//        if(jumlah > 0) {
//            badge.setText("" + jumlah);
//            badge.show();
//            ShortcutBadger.applyCount(getContext(), jumlah);
//        }else{
//            ShortcutBadger.removeCount(getContext());
//            badge.hide();
//        }
//    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(getContext(), "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(getContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            // Launching the login activity
            Intent intent = new Intent(getContext(), panduan.class);
            getActivity().finish();
            startActivity(intent);
        }else{
            Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean hasPermissions(@NonNull String... permissions) {
        for (String permission : permissions)
            if (PackageManager.PERMISSION_GRANTED != getActivity().checkSelfPermission(permission))
                return false;
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        final FragmentManager fragManager = this.getChildFragmentManager();
        final Fragment fragment = fragManager.findFragmentById(R.id.map);
        if(fragment!=null){
            fragManager.beginTransaction().remove(fragment).commit();
            mMap=null;
        }
    }
}
