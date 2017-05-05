package com.garudatekno.jemaah.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
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
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
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

import static java.sql.Types.NULL;

public class profile extends AppCompatActivity implements OnClickListener {
    private TextView txtpemimpin,txtName, txtPhone, txtPassport, editTextuser, txtEmail,txtAddress,txtTwon,txtProvince,txttravel,txtmekkah,txtmadinah,txtpembimbing;
    private Button buttonAdd, buttonLogout,buttonpembimbing,buttonPemimpin;
    private CircleImageView imgProfile;
    String lat,lng,uid;
    //user
    private SQLiteHandler db;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        File folder = new File("/sdcard/android/data/com.garudatekno.jemaah/images");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

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
        //useri
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/profile.png");
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
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(profile.this, img_setting);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.logout) {
                            logoutUser();
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

//CONTENT
        txtName = (TextView) findViewById(R.id.name);
        txtAddress = (TextView) findViewById(R.id.address);
        txtPhone = (TextView) findViewById(R.id.phone);
        txtPassport = (TextView) findViewById(R.id.passport);
        txtProvince = (TextView) findViewById(R.id.province);
        txttravel = (TextView) findViewById(R.id.travel_agent);
        txtmekkah = (TextView) findViewById(R.id.hotel_mekkah);
        txtmadinah = (TextView) findViewById(R.id.hotel_madinah);
        txtpembimbing = (TextView) findViewById(R.id.pembimbing);
        txtTwon = (TextView) findViewById(R.id.town);
        editTextuser = (TextView) findViewById(R.id.userid);
        txtpemimpin = (TextView) findViewById(R.id.pemimpin);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        getData();

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonpembimbing = (Button) findViewById(R.id.btnpembimbing);
        buttonPemimpin = (Button) findViewById(R.id.btnpemimpin);
        imgProfile = (CircleImageView) findViewById(R.id.imageProfile);
        buttonPemimpin.setOnClickListener(this);
        buttonpembimbing.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

    }

    public void onClick(View v){
        if(v == buttonAdd){
            Intent i = new Intent(getApplicationContext(), edit_profile.class);
            startActivity(i);
         }if(v == buttonLogout){
            logoutUser();
        }if(v == buttonpembimbing){
            Intent i = new Intent(getApplicationContext(), PenilaianPembimbing.class);
            startActivity(i);
        }if(v == buttonPemimpin){
            Intent i = new Intent(getApplicationContext(), PenilaianPemimpinTur.class);
            startActivity(i);
        }

    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

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
            String name = c.getString(AppConfig.KEY_NAME);
            String address = c.getString(AppConfig.KEY_ADDRESS);
            String passport = c.getString(AppConfig.KEY_PASSPORT);
            String phone = c.getString(AppConfig.KEY_PHONE);
            String province = c.getString(AppConfig.KEY_PROVINCE);
            String town = c.getString(AppConfig.KEY_TOWN);
            String travel = c.getString(AppConfig.KEY_TRAVEL_AGENT);
            String mekkah = c.getString(AppConfig.KEY_HOTEL_MEKKAH);
            String madinah = c.getString(AppConfig.KEY_HOTEL_MADINAH);
            String pembimbing = c.getString(AppConfig.KEY_PEMBIMBING);
            String nilai_pemb = c.getString(AppConfig.KEY_NILAI_PEMBIMBING);
            String pemimpin = c.getString(AppConfig.KEY_PEMIMPIN_TUR);
            String nilai_pemim = c.getString(AppConfig.KEY_NILAI_PEMIMPIN_TUR);
            if(!nilai_pemb.equals("-")){
                buttonpembimbing.setVisibility(View.GONE);
            }else{
                buttonpembimbing.setVisibility(View.VISIBLE);
            }

            if(!nilai_pemim.equals("-")){
                buttonPemimpin.setVisibility(View.GONE);
            }else{
                buttonPemimpin.setVisibility(View.VISIBLE);
            }

            if(name.equals(NULL) || name.equals("")) {
                imgProfile.setImageResource(R.drawable.profile);
            }else{
                File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/profile.png");
                if (!file.exists()) {
                    imgProfile.setImageResource(R.drawable.profile);
                }else{
                    Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imgProfile.setImageBitmap(bmp);
                }
            }

            txtName.setText(name);
            txtAddress.setText(address);
            txtPassport.setText(passport);
            txtPhone.setText(phone);
            txtProvince.setText(province);
            txtTwon.setText(town);
            txttravel.setText(travel);
            txtmekkah.setText(mekkah);
            txtmadinah.setText(madinah);
            txtpembimbing.setText(pembimbing);
            txtpemimpin.setText(pemimpin);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
