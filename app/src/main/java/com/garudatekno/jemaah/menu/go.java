package com.garudatekno.jemaah.menu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.GMapV2Direction;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;


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
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        Intent i = getIntent();
        name = i.getStringExtra(AppConfig.KEY_NAME);

        TextView txtpesan= (TextView) findViewById(R.id.txtpesan);
        txtpesan.setText("X  Arah Ke Lokasi "+ name);
        //enable GPS
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync((OnMapReadyCallback) this);
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
                DecimalFormat df = new DecimalFormat("#.####");
                String lats = c.getString(2);
                String lngs = c.getString(3);
//                String lngs = df.format(Float.parseFloat(c.getString(3)));
//                Log.d("hasilnya",lats+" - "+c.getString(2));
//                Log.d("hasilnya",lngs+" - "+c.getString(3));
                LatLng toPosition = new LatLng(Double.parseDouble(lats), Double.parseDouble(lngs));

//            LatLng fromPosition = new LatLng(-6.3039, 106.8267);
//            LatLng toPosition = new LatLng(-6.29436, 106.8859);
                md = new GMapV2Direction();

//            LatLng coordinates = new LatLng(-6.3039, 106.8267);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fromPosition, 17));

                mMap.addMarker(new MarkerOptions().position(fromPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .title("Lokasi Saya"));
                mMap.addMarker(new MarkerOptions().position(toPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(name));
                getDirectionMap(fromPosition, toPosition);
            }else{
                Toast.makeText(getApplicationContext(),"Location : "+location, Toast.LENGTH_LONG).show();
            }
            mMap.setMyLocationEnabled(true);

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
                Intent i = new Intent(getApplicationContext(), setting.class);
                startActivity(i);
            }
        });

        editTextuser = (EditText) findViewById(R.id.userid);
        txtlat = (EditText) findViewById(R.id.lat);
        txtlng = (EditText) findViewById(R.id.lng);

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
        if (session.isLoggedIn()) {
            session.setLogin(false);
            db.deleteUsers();
        }

        // Launching the login activity
        Intent intent = new Intent(go.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
