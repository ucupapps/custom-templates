package com.garudatekno.jemaah.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.readystatesoftware.viewbadger.BadgeView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;

public class PintuMasjid extends AppCompatActivity {

    private static final String TAG = "Laporkan";
    EditText txtmasalah;
    ImageView image;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1,REQUEST_CAMERA = 0;
    private Uri filePath;
    String id,uid;
    private SQLiteHandler db;
    View target ;
    BadgeView badge ;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pintu_masjid);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);
        //badge
        target = findViewById(R.id.img_inbox);
        badge = new BadgeView(this, target);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            CountInbox();
        }

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

        image =(ImageView) findViewById(R.id.cameraPintu);
        Button buttonSimpan =(Button) findViewById(R.id.buttonSimpanPintu);
        buttonSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBarcode();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });
        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/pintuMasjid.jpg");
        if (!file.exists()) {
            image.setImageResource(R.drawable.camera);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            image.setImageBitmap(bmp);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null && data.getData() != null) {

//            filePath = data.getData();
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                image.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");

            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            image.setImageBitmap(thumbnail);
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
                loading = ProgressDialog.show(PintuMasjid.this,"","Laporkan masalah...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(PintuMasjid.this, s, Toast.LENGTH_LONG).show();
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

        Intent intent = new Intent(PintuMasjid.this, laporkanmasalah.class);
        startActivity(intent);
    }
    protected void CountInbox(){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String hsl = s.trim();
                Integer a = Integer.parseInt(hsl);
                if(a > 0){
                    badge.setText(hsl);
                    badge.show();
                    ShortcutBadger.applyCount(getApplicationContext(), a);
                }else {
                    ShortcutBadger.removeCount(getApplicationContext());
                    badge.hide();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(AppConfig.URL_COUNT_INBOX,uid);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

}
