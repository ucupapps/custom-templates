package com.garudatekno.jemaah.menu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import com.garudatekno.jemaah.activity.GPSTracker;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class emergency extends AppCompatActivity implements OnClickListener, OnMapReadyCallback {
    private EditText editTextuser;
    private Button buttonAdd;
    private Bitmap bitmap;
    private RadioGroup rg;
    private static final int PICK_Camera_IMAGE = 2;
    Uri imageUri;
    String lat, lng, uid;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    //user
    private SQLiteHandler db;
    private SessionManager session;
    private GoogleMap mMap;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency);

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
        txt_emergency.setTextColor(Color.WHITE);
        menu_emergency.setBackgroundResource(R.color.colorPrimary);
        ImageView img_doa = (ImageView) findViewById(R.id.img_emergency);
        img_doa.setImageDrawable(getResources().getDrawable(R.drawable.emergency_hover));
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

        //FOOTER
        TextView txt_thowaf = (TextView) findViewById(R.id.txt_thowaf);
        TextView txt_go = (TextView) findViewById(R.id.txt_go);
        TextView txt_sai = (TextView) findViewById(R.id.txt_sai);
        txt_thowaf.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });


        editTextuser = (EditText) findViewById(R.id.userid);
        editTextuser.setVisibility(View.GONE);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        //user
        editTextuser.setText(uid);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng jakarta = new LatLng(-6.2268682, 106.8289868);
        mMap.addMarker(new MarkerOptions().position(jakarta).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(jakarta));
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
        if (mMap != null) {


            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {
                    // TODO Auto-generated method stub

                    mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                }
            });
        }
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(emergency.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

        public void onClick(View v){
        if(v == buttonAdd){
           addBarcode();

        }
    }

    //Adding an addBarcode
    private void addBarcode(){
        final String idUser = editTextuser.getText().toString().trim();;

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
                data.put(AppConfig.KEY_LAT, "-6.2268682");
                data.put(AppConfig.KEY_LNG, "106.8289868");
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
