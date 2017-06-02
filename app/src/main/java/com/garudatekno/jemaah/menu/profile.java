package com.garudatekno.jemaah.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.app.AppController;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;

public class profile extends AppCompatActivity implements OnClickListener {
    private TextView txtpemimpin,txtName, txtPhone, txtPassport, editTextuser, txtEmail,txtAddress,txtTwon,txtProvince,txttravel,txtmekkah,txtmadinah,txtpembimbing,
                        txtTravelPhone,txtPemimpinPhone,txtPembimbingPhone,txtEmailProfile,pFamily1,eFamily1,pFamily2,eFamily2,pFamily3,eFamily3;
    private Button buttonAdd, buttonLogout,buttonpembimbing,buttonPemimpin;
    private CircleImageView imgProfile,imgAgentProfile,imgPemimpinProfile,imgPembimbingProfile;
    String lat,lng,uid,emailUser;
    //user
    private SQLiteHandler db;
    private SessionManager session;
    View target ;
    BadgeView badge ;
    private SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        File folder = new File("/sdcard/android/data/com.garudatekno.jemaah/images");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);
        final LinearLayout konten= (LinearLayout) findViewById(R.id.konten);
        final TextView txtkoneksi= (TextView) findViewById(R.id.txtkoneksi);

        if (!cek_status(getApplicationContext()))
        {
            txtkoneksi.setVisibility(View.VISIBLE);
        }else{
            txtkoneksi.setVisibility(View.GONE);
        }

        //tracker
        AppController application = (AppController) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        // [START screen_view_hit]
        mTracker.setScreenName("Profile");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        database = openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        CountInbox();
//        session = new SessionManager(getApplicationContext());

        emailUser = user.get("email");

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
        //useri
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/profile.png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }

//        Picasso.with(this).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/images.jpg").error(R.drawable.profile).into(imgp);

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

//CONTENT
        txtName = (TextView) findViewById(R.id.name);
        txtAddress = (TextView) findViewById(R.id.address);
        txtPhone = (TextView) findViewById(R.id.phone);
        txtPassport = (TextView) findViewById(R.id.passport);
        txtProvince = (TextView) findViewById(R.id.province);
        txttravel = (TextView) findViewById(R.id.travel_agent);
        txtTravelPhone = (TextView) findViewById(R.id.travel_agent_phone);
//        txtmekkah = (TextView) findViewById(R.id.hotel_mekkah);
//        txtmadinah = (TextView) findViewById(R.id.hotel_madinah);
        txtpembimbing = (TextView) findViewById(R.id.pembimbing);
        txtPembimbingPhone = (TextView) findViewById(R.id.pembimbing_phone);
        txtTwon = (TextView) findViewById(R.id.town);
        editTextuser = (TextView) findViewById(R.id.userid);
        txtpemimpin = (TextView) findViewById(R.id.pemimpin);
        txtPemimpinPhone = (TextView) findViewById(R.id.pemimpin_phone);
        txtEmailProfile = (TextView) findViewById(R.id.emailProfile);
        pFamily1 = (TextView) findViewById(R.id.pFamily1);
        eFamily1 = (TextView) findViewById(R.id.eFamily1);
        pFamily2 = (TextView) findViewById(R.id.pFamily2);
        eFamily2 = (TextView) findViewById(R.id.eFamily2);
        pFamily3 = (TextView) findViewById(R.id.pFamily3);
        eFamily3 = (TextView) findViewById(R.id.eFamily3);


        if (session.isLoggedIn()) {
            getData();
        }
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
//        buttonpembimbing = (Button) findViewById(R.id.btnpembimbing);
//        buttonPemimpin = (Button) findViewById(R.id.btnpemimpin);

        imgProfile = (CircleImageView) findViewById(R.id.imageProfile);
        imgAgentProfile = (CircleImageView) findViewById(R.id.imageAgentProfile);
        imgPembimbingProfile = (CircleImageView) findViewById(R.id.imagePembimbingProfile);
        imgPemimpinProfile = (CircleImageView) findViewById(R.id.imagePemimpinProfile);

//        buttonPemimpin.setOnClickListener(this);
//        buttonpembimbing.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

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

    public void onClick(View v){
        if(v == buttonAdd){
            Intent i = new Intent(getApplicationContext(), edit_profile.class);
            startActivity(i);
         }if(v == buttonLogout){
            logoutUser();
        }
//        if(v == buttonpembimbing){
//            Intent i = new Intent(getApplicationContext(), PenilaianPembimbing.class);
//            startActivity(i);
//        }if(v == buttonPemimpin){
//            Intent i = new Intent(getApplicationContext(), PenilaianPemimpinTur.class);
//            startActivity(i);
//        }

    }

    private void logoutUser() {
        if (session.isLoggedIn()) {
            session.setLogin(false);

            db.deleteUsers();
        }

        // Launching the login activity
        Intent intent = new Intent(profile.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void getData(){
        class GetData extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(profile.this,"","Harap Tunggu...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
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
            String name = c.getString(AppConfig.KEY_NAME);
            String address = c.getString(AppConfig.KEY_ADDRESS);
            String passport = c.getString(AppConfig.KEY_PASSPORT);
            String phone = c.getString(AppConfig.KEY_PHONE);
            String province = c.getString(AppConfig.KEY_PROVINCE);
            String town = c.getString(AppConfig.KEY_TOWN);
            String travel = c.getString(AppConfig.KEY_TRAVEL_AGENT);
            String travel_phone = c.getString(AppConfig.KEY_TRAVEL_PHONE);
            String mekkah = c.getString(AppConfig.KEY_HOTEL_MEKKAH);
            String madinah = c.getString(AppConfig.KEY_HOTEL_MADINAH);
            String pembimbing = c.getString(AppConfig.KEY_PEMBIMBING);
            String pembimbing_phone = c.getString(AppConfig.KEY_PEMBIMBING_PHONE);
//            String nilai_pemb = c.getString(AppConfig.KEY_NILAI_PEMBIMBING);
            String pemimpin = c.getString(AppConfig.KEY_PEMIMPIN_TUR);
            String pemimpin_phone = c.getString(AppConfig.KEY_PEMIMPIN_PHONE);
            String tfamily1 = c.getString(AppConfig.KEY_PHONE_FAMILY1);
            String tfamily2 = c.getString(AppConfig.KEY_PHONE_FAMILY2);
            String tfamily3 = c.getString(AppConfig.KEY_PHONE_FAMILY3);
            String efamily1 = c.getString(AppConfig.KEY_EMAIL_FAMILY1);
            String efamily2 = c.getString(AppConfig.KEY_EMAIL_FAMILY2);
            String efamily3 = c.getString(AppConfig.KEY_EMAIL_FAMILY3);
//            String nilai_pemim = c.getString(AppConfig.KEY_NILAI_PEMIMPIN_TUR);
//            if(!nilai_pemb.equals("-")){
//                buttonpembimbing.setVisibility(View.GONE);
//            }else{
//                buttonpembimbing.setVisibility(View.VISIBLE);
//            }
//
//            if(!nilai_pemim.equals("-")){
//                buttonPemimpin.setVisibility(View.GONE);
//            }else{
//                buttonPemimpin.setVisibility(View.VISIBLE);
//            }

//            if(name.equals(NULL) || name.equals("")) {
//                imgProfile.setImageResource(R.drawable.profile);
//            }else{
                File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/profile.png");
                if (!file.exists()) {
                    imgProfile.setImageResource(R.drawable.profile);
                }else{
                    Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imgProfile.setImageBitmap(bmp);
                }
//            }

//            Picasso.with(this).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/images.jpg").error(R.drawable.profile).into(imgProfile);
            Picasso.with(this).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/agent.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).error(R.drawable.profile).into(imgAgentProfile);
            Picasso.with(this).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/pembimbing.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).error(R.drawable.profile).into(imgPembimbingProfile);
            Picasso.with(this).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/pemimpin.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).error(R.drawable.profile).into(imgPemimpinProfile);

            txtName.setText(name);
            txtAddress.setText(address);
            txtPassport.setText(passport);
            txtPhone.setText(phone);
            txtProvince.setText(province);
            txtTwon.setText(town);
            txttravel.setText(travel);
//            txtmekkah.setText(mekkah);
//            txtmadinah.setText(madinah);
            txtpembimbing.setText(pembimbing);
            txtpemimpin.setText(pemimpin);
            txtPemimpinPhone.setText(pemimpin_phone);
            txtPembimbingPhone.setText(pembimbing_phone);
            txtTravelPhone.setText(travel_phone);
            txtEmailProfile.setText(emailUser);
            pFamily1.setText(tfamily1);
            pFamily2.setText(tfamily2);
            pFamily3.setText(tfamily3);
            eFamily1.setText(efamily1);
            eFamily2.setText(efamily2);
            eFamily3.setText(efamily3);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void CountInbox(){
        Cursor c= database.rawQuery("select * from badge where id=1 ", null);
        c.moveToFirst();
        int jumlah=c.getInt(1);
        target = findViewById(R.id.img_inbox);
        badge = new BadgeView(this, target);
        //badge
        if(jumlah > 0) {
            badge.setText("" + jumlah);
            badge.show();
            ShortcutBadger.applyCount(getApplicationContext(), jumlah);
        }else{
            ShortcutBadger.removeCount(getApplicationContext());
            badge.hide();
        }
    }

}
