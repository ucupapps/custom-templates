package com.garudatekno.jemaah.menu;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.readystatesoftware.viewbadger.BadgeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;

import static java.lang.Boolean.FALSE;

public class setting extends AppCompatActivity {
    LinearLayout txtshare,txtdownload,txtdonasi,txtpenilaian,txtcekvisa,txtsyarat,txtbantuan;
    private Button buttonLogout;
    //user
    String uid;
    private SQLiteHandler db;
    private SessionManager session;
    View target ;
    BadgeView badge ;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        session = new SessionManager(getApplicationContext());
        database = openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        CountInbox();

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

        menu_profile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), profile.class);
                startActivity(i);
                finish();
            }
        });
        menu_panduan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
                finish();
            }
        });
        menu_doa.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TitipanDoa.class);
                startActivity(i);
                finish();
            }
        });
        menu_navigasi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), navigasi.class);
                startActivity(i);
                finish();
            }
        });
        menu_inbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), inbox.class);
                startActivity(i);
                finish();
            }
        });

        final ImageView img_home=(ImageView) findViewById(R.id.img_home);
        img_home.setOnClickListener(new OnClickListener() {
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

        txtshare = (LinearLayout) findViewById(R.id.txtshare);
        txtbantuan = (LinearLayout) findViewById(R.id.txtbantuan);
        txtpenilaian = (LinearLayout) findViewById(R.id.txtpenilaian);
        txtcekvisa = (LinearLayout) findViewById(R.id.txtcekvisa);
        txtsyarat = (LinearLayout) findViewById(R.id.txtsyarat);
        txtdonasi = (LinearLayout) findViewById(R.id.txtdonasi);
        txtdownload = (LinearLayout) findViewById(R.id.txtdownload);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        if (!session.isLoggedIn()) {
            buttonLogout.setVisibility(View.GONE);
        }

        txtdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), download.class);
                startActivity(i);
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        txtshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "GoHajj");
                    String sAux = "\nPerjalanan haji dan umroh mabrur dengan  mudah, aman, tanpa ragu, " +
                            "dan terpandu? Download aplikasi GoHajj, teman perjalanan ke Rumah Allah. https://play.google.com/store/apps/details?id=GoHajj\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });
        txtbantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Bantuan.class);
                startActivity(i);
            }
        });
        txtpenilaian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PenilaianTravel.class);
                startActivity(i);
            }
        });
        txtcekvisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uriUrl = Uri.parse("https://eservices.haj.gov.sa/eservices3/pages/VisaInquiry/SearchVisa.xhtml?dswid=4963");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
        txtsyarat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SyaratKetentuan.class);
                startActivity(i);
            }
        });
        txtdonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uriUrl = Uri.parse("https://kitabisa.com/gohaji");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

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

    private void logoutUser() {
        if (session.isLoggedIn()) {
            session.setLogin(false);
            db.deleteUsers();
        }

        // Launching the login activity
        Intent intent = new Intent(setting.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
