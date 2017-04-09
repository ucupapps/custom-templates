package com.garudatekno.jemaah.menu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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
import com.garudatekno.jemaah.activity.GMapV2Direction;
import com.garudatekno.jemaah.activity.GMapV2DirectionAsyncTask;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.MainActivity;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;


public class go extends AppCompatActivity {
    private EditText editTextuser, txtMessage, txtphone, txtlng, txtlat;
    private Button btnbus, btnhotel, btnmeeting, btnpintu;
    private Bitmap bitmap;
    private RadioGroup rg;
    private static final int PICK_Camera_IMAGE = 2;
    Uri imageUri;
    String pesan, phone, uid,name;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    //user
    private SQLiteHandler db;
    private SessionManager session;
    private GoogleMap mMap;
    Location location;
    GMapV2Direction md;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.go);
        Intent i = getIntent();

        name = i.getStringExtra(AppConfig.KEY_NAME);
        //enable GPS
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // get the last know location from your location manager.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                LatLng fromPosition = new LatLng(location.getLatitude(), location.getLongitude());
//
                database = openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
                Cursor c = database.rawQuery("SELECT * FROM locations WHERE name='" + name + "'", null);
                c.moveToFirst();
                String lats = c.getString(2);
                String lngs = c.getString(3);
                LatLng toPosition = new LatLng(Double.parseDouble(lats), Double.parseDouble(lngs));

//            LatLng fromPosition = new LatLng(-6.3039, 106.8267);
//            LatLng toPosition = new LatLng(-6.29436, 106.8859);
                md = new GMapV2Direction();

//            LatLng coordinates = new LatLng(-6.3039, 106.8267);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fromPosition, 17));

                mMap.addMarker(new MarkerOptions().position(fromPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .title("Your Location"));
                mMap.addMarker(new MarkerOptions().position(toPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(name));
                getDirectionMap(fromPosition, toPosition);
            }else{
                Toast.makeText(getApplicationContext(),"Location : "+location, Toast.LENGTH_LONG).show();
            }
            mMap.setMyLocationEnabled(true);

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
                PopupMenu popup = new PopupMenu(go.this, txt_go);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.bus) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"BUS");
                            finish();
                            startActivity(i);
                        }
                        if(id == R.id.hotel) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"HOTEL");
                            finish();
                            startActivity(i);
                        }
                        if(id == R.id.pintu) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"NO PINTU MASJID");
                            finish();
                            startActivity(i);
                        }
                        if(id == R.id.meeting) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"MEETING POINT");
                            finish();
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


        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        phone = user.get("family_phone");
        //user
        editTextuser.setText(uid);

    }

    private void getDirectionMap(LatLng from, LatLng to) {
       LatLng fromto[] = { from, to };
       new LongOperation().execute(fromto);

    }

    private class LongOperation extends AsyncTask<LatLng, Void, Document> {
        @Override
        protected Document doInBackground(LatLng... params) {
            Document doc = md.getDocument(params[0], params[1],
                    GMapV2Direction.MODE_WALKING);
            return doc;

        }

        @Override
        protected void onPostExecute(Document result) {
           setResult(result);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    public void setResult(Document doc) {
        int duration = md.getDurationValue(doc);
        String distance = md.getDistanceText(doc);
        String start_address = md.getStartAddress(doc);
        String copy_right = md.getCopyRights(doc);

        ArrayList<LatLng> directionPoint = md.getDirection(doc);
        PolylineOptions rectLine = new PolylineOptions().width(3).color(
                Color.RED);

        for (int i = 0; i < directionPoint.size(); i++) {
            rectLine.add(directionPoint.get(i));

        }

        mMap.addPolyline(rectLine);
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(go.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
