package salam.gohajj.custom.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import salam.gohajj.custom.GetTemplates;
import salam.gohajj.custom.Interfaces;
import salam.gohajj.custom.R;
import salam.gohajj.custom.Utilities;
import salam.gohajj.custom.activity.LoginActivity;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;

import com.github.clans.fab.FloatingActionButton;
import com.readystatesoftware.viewbadger.BadgeView;
import com.github.clans.fab.FloatingActionMenu;


import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;

public class setting extends AppCompatActivity {
    LinearLayout txtshare,txtdownload,txtdonasi,txtpilihtema,txtpenilaian,txtcekvisa,txtsyarat,txtbantuan,txtlanguage;
    private Button buttonLogout;
    //user
    String uid;
    private SQLiteHandler db;
    private SessionManager session;
    View target ;
    BadgeView badge ;
    private SQLiteDatabase database;    private Activity activity;

    private LinearLayout footerMenu;
    private FloatingActionMenu floatingMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        activity = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final RelativeLayout rel_header = (RelativeLayout) findViewById(R.id.header2);
        GetTemplates.GetStatusBar(activity);
        rel_header.setBackground(getResources().getDrawable(GetTemplates.GetHeaderTemplates(activity)));
        //Calligrapher calligrapher=new Calligrapher(this);
        //calligrapher.setFont(this,"fonts/helvetica.ttf",true);
        ChooseMenu();
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        session = new SessionManager(getApplicationContext());
        database = openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        CountInbox();


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
                //Intent i = new Intent(getApplicationContext(), setting.class);
                //startActivity(i);
                //finish();
            }
        });

        txtshare = (LinearLayout) findViewById(R.id.txtshare);
        txtbantuan = (LinearLayout) findViewById(R.id.txtbantuan);
        txtpenilaian = (LinearLayout) findViewById(R.id.txtpenilaian);
        txtcekvisa = (LinearLayout) findViewById(R.id.txtcekvisa);
        txtsyarat = (LinearLayout) findViewById(R.id.txtsyarat);
        txtpilihtema = (LinearLayout) findViewById(R.id.txttpilihtema);
        txtdonasi = (LinearLayout) findViewById(R.id.txtdonasi);
        txtdownload = (LinearLayout) findViewById(R.id.txtdownload);
        txtlanguage = (LinearLayout) findViewById(R.id.txtlanguage);
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

        txtlanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), language.class);
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
                            "dan terpandu? Download aplikasi GoHajj, teman perjalanan ke Rumah Allah. https://play.google.com/store/apps/details?id=salam.gohajj.custom\n";
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
        txtpilihtema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PilihTema.class);
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
        File file = new File("/sdcard/android/data/salam.gohajj.custom/images/"+uid+".png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }
    }


    public void ChooseMenu(){
        String getpref = Utilities.getPref("id_pref",activity)!=null? Utilities.getPref("id_pref",activity):"";
        footerMenu = (LinearLayout)findViewById(R.id.menufooter);
        floatingMenu=(FloatingActionMenu)findViewById(R.id.fabmenu);
        if (getpref.equals(Interfaces.TEMPLATE_1)){
            //Floating Menu
            footerMenu.setVisibility(View.GONE);
            floatingMenu.setVisibility(View.GONE);

        }else if (getpref.equals(Interfaces.TEMPLATE_3)){
            //Floating Menu
            footerMenu.setVisibility(View.GONE);
            floatingMenu.setVisibility(View.VISIBLE);
            // New FAB
            FloatingActionButton fab1=(FloatingActionButton)findViewById(R.id.fabpanduan);
            FloatingActionButton fab2=(FloatingActionButton)findViewById(R.id.fabdoa);
            FloatingActionButton fab3=(FloatingActionButton)findViewById(R.id.fabnavigation);
            FloatingActionButton fab4=(FloatingActionButton)findViewById(R.id.fabinbox);
            FloatingActionButton fab5=(FloatingActionButton)findViewById(R.id.fabprofile);

        }
        else {
            //Footer Menu
            floatingMenu.setVisibility(View.GONE);
            footerMenu.setVisibility(View.VISIBLE);
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
        };

    }

    private void logoutUser() {
        if (session.isLoggedIn()) {
            session.setLogin(false);
            Utilities.putPref("id_pref",Interfaces.TEMPLATE_DEFAULT,activity);
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), panduan.class);
        panduan.setTabIndex(2);
        startActivity(i);
        finish();
    }
}
