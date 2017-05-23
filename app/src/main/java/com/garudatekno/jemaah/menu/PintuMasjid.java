package com.garudatekno.jemaah.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
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
import java.io.FileOutputStream;
import java.util.HashMap;

import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;

public class PintuMasjid extends AppCompatActivity {

    private static final String TAG = "Laporkan";
    EditText txtCaptionPintu;
    ImageView image;
    private Bitmap thumbnail;
    private String selectedCamera;
    private int PICK_IMAGE_REQUEST = 1,REQUEST_CAMERA = 0;
    private Uri filePath;
    String id,uid;
    private SQLiteHandler db;
    View target ;
    BadgeView badge ;
    private SessionManager session;

    private SQLiteDatabase database;

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
        createDatabase();
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

        image =(ImageView) findViewById(R.id.cameraPintu);
        txtCaptionPintu = (EditText) findViewById(R.id.captionPintu);
        Button buttonSimpan =(Button) findViewById(R.id.buttonSimpanPintu);
        Button buttonBatal =(Button) findViewById(R.id.buttonCancelPintu);
        buttonSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtCaptionPintu.getText().toString().equals("")) {
                    Toast.makeText(PintuMasjid.this, "No Pintu Tidak Boleh Kosong !", Toast.LENGTH_SHORT).show();
                }else{
                    simpanPintu();
                    insertIntoMasjid(uid,txtCaptionPintu.getText().toString());
                    Toast.makeText(PintuMasjid.this, "Simpan Berhasil...", Toast.LENGTH_LONG).show();
                    Intent j = new Intent(getApplicationContext(), navigasi.class);
                    startActivity(j);
                }
            }
        });
        buttonBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), navigasi.class);
                startActivity(i);
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCamera = "selected";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });
//        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/pintuMasjid.jpg");
//        if (!file.exists()) {
//            image.setImageResource(R.drawable.camera);
//        }else{
//            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
//            image.setImageBitmap(bmp);
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null && data.getExtras().get("data") != null) {

            Log.e("kosong", String.valueOf(data.getExtras().get("data")));
            if(selectedCamera != null) {
                thumbnail = (Bitmap) data.getExtras().get("data");
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

//            File destination = new File(Environment.getExternalStorageDirectory(),
//                    System.currentTimeMillis() + ".jpg");
//
//            FileOutputStream fo;
//            try {
//                destination.createNewFile();
//                fo = new FileOutputStream(destination);
//                fo.write(bytes.toByteArray());
//                fo.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

                image.setImageBitmap(thumbnail);
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

    private void simpanPintu(){
        if(selectedCamera != null) {
            try {
//                    String uploadImageProfile = getStringImage(bitmapProfile);
//                    Log.e("profile",uploadImageProfile);
//                    data.put(AppConfig.UPLOAD_KEY, uploadImageProfile);

                //save image to sdcard
                File folder = new File("/sdcard/android/data/com.garudatekno.jemaah/images");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File sdCardDirectory = Environment.getExternalStorageDirectory();
                File image = new File(sdCardDirectory + "/android/data/com.garudatekno.jemaah/images/", "pintuMasjid.jpg");
                // Encode the file as a PNG image.
                FileOutputStream outStream;

                outStream = new FileOutputStream(image);
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    /* 100 to keep full quality of the image */
                outStream.flush();
                outStream.close();
            } catch (Exception ex) {
//                    data.put(AppConfig.UPLOAD_KEY, "");
                Log.i("Bitmap Error", "Tidak ada image");
            }
        }
    }

    //Adding an addBarcode
    private void addBarcode(){
        final String masalah = txtCaptionPintu.getText().toString().trim();

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
        ae.execute(thumbnail);

//        Intent intent = new Intent(PintuMasjid.this, laporkanmasalah.class);
//        startActivity(intent);
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

    protected void createDatabase(){
        database=openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS pintu_masjid(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, user_id VARCHAR,no_pintu VARCHAR);");
    }

    protected void insertIntoMasjid(String user_id,String no_pintu){

        Cursor mCount= database.rawQuery("select count(*) from pintu_masjid where user_id='" + user_id + "'", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        if(count > 0) {
            String query = "UPDATE pintu_masjid SET no_pintu='" + no_pintu + "' WHERE user_id='" + user_id + "';";
            database.execSQL(query);
        }else {
            String query = "INSERT INTO pintu_masjid (user_id,no_pintu) VALUES('" + user_id + "', '" + no_pintu + "');";
            database.execSQL(query);
        }
//        Toast.makeText(getApplicationContext(),"Pintu Masjid "+no_pintu+ " Berhasil di simpan", Toast.LENGTH_LONG).show();
        Cursor c = database.rawQuery("SELECT * FROM pintu_masjid WHERE user_id='" + user_id + "'", null);

        c.moveToFirst();
        String name=c.getString(1);
        String no=c.getString(2);
        Log.d("MyDataShow", "user_id: " + name+"no_pintu: " + no);
    }

}
