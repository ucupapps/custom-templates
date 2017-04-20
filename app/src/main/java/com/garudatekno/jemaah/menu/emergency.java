package com.garudatekno.jemaah.menu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.MainActivity;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public class emergency extends AppCompatActivity implements OnClickListener, OnMapReadyCallback  {
    private EditText editTextuser,txtMessage,txtphone,txtlng,txtlat;
    private Button buttonAdd;
    private Bitmap bitmap;
    private RadioGroup rg;
    private static final int PICK_Camera_IMAGE = 2;
    Uri imageUri;
    String lat, phone, uid;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    //user
    private SQLiteHandler db;
    private SessionManager session;
    private GoogleMap mMap;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency);
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
//        TextView txt_emergency=(TextView) findViewById(R.id.txt_emergency);
//        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
//        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
//        txt_thowaf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(i);
//            }
//        });
//        txt_sai.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), sai.class);
//                startActivity(i);
//            }
//        });
//        txt_emergency.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), emergency.class);
//                startActivity(i);
//            }
//        });

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
                Intent i = new Intent(getApplicationContext(), Doa.class);
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
        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
            }
        });
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(emergency.this, img_setting);
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
        txtphone = (EditText) findViewById(R.id.phone);
        txtMessage = (EditText) findViewById(R.id.message);
        txtlat = (EditText) findViewById(R.id.lat);
        txtlng = (EditText) findViewById(R.id.lng);
        editTextuser.setVisibility(View.GONE);
        txtphone.setVisibility(View.GONE);
        txtlat.setVisibility(View.GONE);
        txtlng.setVisibility(View.GONE);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        phone = user.get("family_phone");
        //user
        editTextuser.setText(uid);
        txtphone.setText(phone);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);
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
//                emergency.this,
//                "Lat " + location.getLatitude() + " "
//                        + "Long " + location.getLongitude(),
//                Toast.LENGTH_LONG).show();
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(emergency.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            String numbers[] = phoneNo.split(", *");
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(msg);

            for(String number : numbers) {
                smsManager.sendMultipartTextMessage(number, null, parts, null, null);
            }
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void onClick(View v){
        if(v == buttonAdd){
            getCurrentLocation();
            if(txtMessage.getText().toString().equals(""))
            {
                Toast.makeText(this, "Message cannot null", Toast.LENGTH_SHORT).show();
            }else if(txtphone.getText().toString().equals(""))
            {
                Toast.makeText(this, "Family phone cannot null", Toast.LENGTH_SHORT).show();
            }else if(txtlat.getText().toString().equals(""))
            {
                Toast.makeText(this, "Current location cannot null !", Toast.LENGTH_SHORT).show();
            }else {
//            String phoneNumber = "082113150425,085229296292,081328280585";
                String phoneNumber = txtphone.getText().toString();
                String message = txtMessage.getText().toString();
                sendSMS(phoneNumber, message);
                addBarcode();
            }
        }
    }

    //Adding an addBarcode
    private void addBarcode(){
        final String idUser = editTextuser.getText().toString().trim();
        final String lat = txtlat.getText().toString().trim();
        final String lng = txtlng.getText().toString().trim();
        final String message = txtMessage.getText().toString().trim();

        class AddBarcode extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(emergency.this,"Sending Message","...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(emergency.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_USERID, idUser);
                data.put(AppConfig.KEY_LAT, lat);
                data.put(AppConfig.KEY_LNG, lng);
                data.put(AppConfig.KEY_MESSAGE, message);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(AppConfig.URL_EMERGENCY, data);
                return res;
            }
        }

        AddBarcode ae = new AddBarcode();
        ae.execute();

        startActivity(new Intent(emergency.this, emergency.class));
    }

}
