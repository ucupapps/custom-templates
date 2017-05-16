package com.garudatekno.jemaah.menu;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;

import static java.lang.Boolean.FALSE;

public class emergency extends AppCompatActivity implements OnClickListener, OnMapReadyCallback  {
    private EditText editTextuser,txtMessage,txtphone,txtlng,txtlat;
    private Button buttonAdd,buttonAdd2,btnaddcontact;
    EditText contact;
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
    private final int PICK_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        session = new SessionManager(getApplicationContext());
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
        txtphone = (EditText) findViewById(R.id.phone);
        txtMessage = (EditText) findViewById(R.id.message);
        txtlat = (EditText) findViewById(R.id.lat);
        txtlng = (EditText) findViewById(R.id.lng);
        editTextuser.setVisibility(View.GONE);
        txtphone.setVisibility(View.GONE);
        txtlat.setVisibility(View.GONE);
        txtlng.setVisibility(View.GONE);

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

        contact = (EditText) findViewById(R.id.contact);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd2 = (Button) findViewById(R.id.buttonAdd2);
        btnaddcontact = (Button) findViewById(R.id.addContact);
        final PopupMenu addkontak = new PopupMenu(this, btnaddcontact);
        addkontak.getMenu().add(1, 1, 1, "Ambil dari kontak");
//        addkontak.getMenu().add(1, 2, 2, "Ketik manual");
        btnaddcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
//                addkontak.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//                        int id = item.getItemId();
//                        if(id == 1) {
//                            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//                            startActivityForResult(intent, PICK_CONTACT);
//                        }if(id == 2) {
//                            final Dialog rankDialog = new Dialog(emergency.this);
//                            rankDialog.setContentView(R.layout.kontak_dialog);
//                            rankDialog.setCancelable(true);
//                            Button btnok = (Button) rankDialog.findViewById(R.id.btnOK);
//                            final EditText nophone=(EditText) rankDialog.findViewById(R.id.nophone);
//
//                            btnok.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                               String cNumber= nophone.getText().toString().trim();
//                                String no=contact.getText().toString().trim();
//                                no += cNumber+",";
//                                contact.setText(no);
//                                contact.setSelection(contact.getText().length());
//                                rankDialog.dismiss();
//                            }
//                        });
//                            rankDialog.getWindow().setLayout(700, 450);
//                            rankDialog.show();
//                        }
//                        return true;
//                    }
//                });
//            addkontak.show();
            }
        });
        btnaddcontact.setTypeface(null, Typeface.BOLD);
        buttonAdd.setOnClickListener(this);
        buttonAdd2.setOnClickListener(this);
//        btnaddcontact.setOnClickListener(this);
        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/profile.png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }
        getCurrentLocation(); getData();
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

        txtMessage.setText("\n\n\n Location: https://maps.google.com/?q="+ location.getLatitude()+"," + location.getLongitude()+"");
//        Toast.makeText(
//                emergency.this,
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
//            Toast.makeText(getApplicationContext(), "Pesan terkirim",
//                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void onClick(View v){
        if(v == buttonAdd2){
            getCurrentLocation();
//            if(contact.getText().toString().equals("") || contact.getText().toString().equals(","))
//            {
//                Toast.makeText(this, "No tujuan tidak boleh kosong !", Toast.LENGTH_SHORT).show();
//            }else{
                addBarcode();
//            }
        }
        if(v == buttonAdd){
            if(txtMessage.getText().toString().equals(""))
            {
                Toast.makeText(this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }else if(contact.getText().toString().equals("") || contact.getText().toString().equals(","))
            {
                Toast.makeText(this, "No tujuan tidak boleh kosong !", Toast.LENGTH_SHORT).show();
            }else {
//            String phoneNumber = "082113150425,085229296292,081328280585";
                String phoneNumber = contact.getText().toString();
                String message = txtMessage.getText().toString();
                sendSMS(phoneNumber, message);
                addBarcode();
            }
        }
        if(v == btnaddcontact){
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex("data1"));
                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            String no=contact.getText().toString().trim();
                            no += cNumber+",";
                            contact.setText(no);
                            contact.setSelection(contact.getText().length());
//                            Toast.makeText(this, name +":"+ cNumber, Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                break;
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

        Intent intent = new Intent(emergency.this, emergency.class);
        startActivity(intent);
        finish();
    }

    private void getData(){
        class GetData extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loading = ProgressDialog.show(profile.this,"Mohon tunggu..."," ",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
                showData(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(AppConfig.URL_GETPROFILE,uid);
                return s;
            }
        }
        GetData ge = new GetData();
        ge.execute();
    }

    private void showData(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            Log.e(AppConfig.TAG_JSON_ARRAY, c.getString(AppConfig.KEY_NAME));
            String phone1 = c.getString(AppConfig.KEY_TRAVEL_PHONE);
            String phone2 = c.getString(AppConfig.KEY_PEMBIMBING_PHONE);
            String phone3 = c.getString(AppConfig.KEY_PEMIMPIN_PHONE);
            String phone4 = c.getString(AppConfig.KEY_PHONE_FAMILY1);
            String phone5 = c.getString(AppConfig.KEY_PHONE_FAMILY2);
            String phone6 = c.getString(AppConfig.KEY_PHONE_FAMILY3);
            if(!phone1.equals("")){
                phone1 +=  ",";
            }if(!phone2.equals("")){
                phone2 +=  ",";
            }if(!phone3.equals("")){
                phone3 += ",";
            }if(!phone4.equals("")){
                phone4 += ",";
            }if(!phone5.equals("")){
                phone5 += ",";
            }if(!phone6.equals("")){
                phone6 += ",";
            }

            contact.setText(phone1+phone2+phone3+phone4+phone5+phone6);
            contact.setSelection(contact.getText().length());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
