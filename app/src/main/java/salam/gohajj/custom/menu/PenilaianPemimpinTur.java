package salam.gohajj.custom.menu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import salam.gohajj.custom.Interfaces;
import salam.gohajj.custom.R;
import salam.gohajj.custom.activity.LoginActivity;
import salam.gohajj.custom.activity.RequestHandler;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;
import com.readystatesoftware.viewbadger.BadgeView;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;

public class PenilaianPemimpinTur extends AppCompatActivity implements OnClickListener {
    private TextView txtName, editTextuser,txttravel,txtpembimbing,txtJudul,txtTanya;
    private EditText textKomen;
    private Button buttonAdd, buttonLogout;
    private CircleImageView imgProfile;
    String lat,lng,uid;
    RatingBar ratingBar ;
    ImageView img;
    View target ;
    BadgeView badge ;
    //user
    private SQLiteHandler db;
    private SessionManager session;
    private SQLiteDatabase database;
    private String getpref;
    private Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.penilaian_pembimbing);
        mActivity = this;
        getpref = salam.gohajj.custom.Utilities.getPref("id_pref",mActivity)!=null? salam.gohajj.custom.Utilities.getPref("id_pref",mActivity):"";
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        session = new SessionManager(getApplicationContext());
        File folder = new File("/sdcard/android/data/salam.gohajj.custom/images");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        session = new SessionManager(getApplicationContext());
        database = openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        CountInbox();

        TextView bullet=(TextView) findViewById(R.id.bullet2);
        bullet.setBackgroundResource(R.drawable.circle_sai_green);
        bullet.setTextColor(Color.WHITE);

        // FOOTER
        LinearLayout footerMenu = (LinearLayout)findViewById(R.id.menufooter);
        if (getpref.equals(Interfaces.TEMPLATE_1)){
            footerMenu.setVisibility(View.GONE);
        }
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
        File file = new File("/sdcard/android/data/salam.gohajj.custom/images/"+uid+".png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }

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


//CONTENT
        txtJudul = (TextView) findViewById(R.id.judul);
        txtTanya = (TextView) findViewById(R.id.pertanyaan);
        txtName = (TextView) findViewById(R.id.name);
        txttravel = (TextView) findViewById(R.id.travel);
        editTextuser = (TextView) findViewById(R.id.userid);
        textKomen = (EditText) findViewById(R.id.komen);
        txtpembimbing = (TextView) findViewById(R.id.txtpembimbing);
        ratingBar = (RatingBar) findViewById(R.id.dialog_ratingbar);
        img = (ImageView) findViewById(R.id.img);
        img.setVisibility(View.VISIBLE);
        txtJudul.setText(getResources().getString(R.string.penilaian_aplikasi));
        txtTanya.setText(getResources().getString(R.string.pertanyaan_aplikasi));

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        txtName.setText("");
        img.setImageResource(R.drawable.logo);
        img.getLayoutParams().width = 600;
        img.getLayoutParams().height = 60;
        editTextuser.setText(uid);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        imgProfile = (CircleImageView) findViewById(R.id.imageProfile);
        imgProfile.setVisibility(View.GONE);
        buttonAdd.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

    }

    public void onClick(View v){
        if(v == buttonAdd){
            String user=uid;
            String rating=String.valueOf(ratingBar.getRating());
            String pembimbing=txtName.getText().toString().trim();
            String komen=textKomen.getText().toString().trim();
            addRating(user,pembimbing,rating,komen);
         }if(v == buttonLogout){
            Intent i = new Intent(getApplicationContext(), profile.class);
            startActivity(i);
            finish();
        }
    }

    private void logoutUser() {
        if (session.isLoggedIn()) {
            session.setLogin(false);
            db.deleteUsers();
        }

        // Launching the login activity
        Intent intent = new Intent(PenilaianPemimpinTur.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void addRating(final String penilai,final String user, final String rate,final String komen){
        class AddBarcode extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(PenilaianPemimpinTur.this,"",getResources().getString(R.string.mengirim)+"...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(PenilaianPemimpinTur.this, getResources().getString(R.string.terima_kasih), Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_USERID, user);
                data.put(AppConfig.KEY_PENILAIID, penilai);
                data.put(AppConfig.KEY_COMMENT, komen);
                data.put(AppConfig.KEY_RATING, rate);
                data.put(AppConfig.KEY_CATEGORY, "PEMIMPIN TUR");
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(AppConfig.URL_RATING, data);
                return res;
            }
        }

        AddBarcode ae = new AddBarcode();
        ae.execute();

        startActivity(new Intent(PenilaianPemimpinTur.this, PenilaianPembimbing.class));
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
