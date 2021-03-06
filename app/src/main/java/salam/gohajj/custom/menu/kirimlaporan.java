package salam.gohajj.custom.menu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import salam.gohajj.custom.GetTemplates;
import salam.gohajj.custom.Interfaces;
import salam.gohajj.custom.R;
import salam.gohajj.custom.Utilities;
import salam.gohajj.custom.activity.RequestHandler;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;
import com.readystatesoftware.viewbadger.BadgeView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;

public class kirimlaporan extends AppCompatActivity {

    private static final String TAG = "Laporkan";
    EditText txtmasalah;
    ImageView image;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    String id,uid;
    private SQLiteHandler db;
    View target ;
    BadgeView badge ;
    private SessionManager session;
    private SQLiteDatabase database;
    private Activity activity;
    private String getpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kirim_laporan);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SetContentView();

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        session = new SessionManager(getApplicationContext());
        database = openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        CountInbox();
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

        TextView txtname =(TextView) findViewById(R.id.name);
        image =(ImageView) findViewById(R.id.image);
        txtmasalah =(EditText) findViewById(R.id.masalah);
        Button buttonAdd =(Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBarcode();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten = new Intent();
                inten.setType("image/*");
                inten.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(inten, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        Intent intent = getIntent();
        id = intent.getStringExtra(AppConfig.EMP_ID);
        txtname.setText(getResources().getString(R.string.laporkan_fitur)+" "+id);
    }

    public void SetContentView(){
        activity = this;
        getpref = Utilities.getPref("id_pref",activity)!=null? Utilities.getPref("id_pref",activity):"";
        GetTemplates.GetStatusBar(activity);
        final RelativeLayout rel_header = (RelativeLayout) findViewById(R.id.header2);
        rel_header.setBackground(getResources().getDrawable(GetTemplates.GetHeaderTemplates(activity)));
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //Adding an addBarcode
    private void addBarcode(){
        final String masalah = txtmasalah.getText().toString().trim();

        class AddBarcode extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(kirimlaporan.this,"",getResources().getString(R.string.laporkan_masalah)+"...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(kirimlaporan.this, getResources().getString(R.string.terimakasih_atas_laporan_anda), Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];

                HashMap<String,String> data = new HashMap<>();
                try{
                    String uploadImage = getStringImage(bitmap);
                    data.put(AppConfig.UPLOAD_KEY, uploadImage);
                } catch (Exception ex) {
                    data.put(AppConfig.UPLOAD_KEY, "");
                }
                data.put(AppConfig.KEY_MASALAH,masalah);
                data.put(AppConfig.KEY_NAME,id);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(AppConfig.URL_LAPORKAN_MASALAH, data);
                return res;
            }
        }


        AddBarcode ae = new AddBarcode();
        ae.execute(bitmap);

        Intent intent = new Intent(kirimlaporan.this, laporkanmasalah.class);
        startActivity(intent);
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
