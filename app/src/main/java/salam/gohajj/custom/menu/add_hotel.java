package salam.gohajj.custom.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import salam.gohajj.custom.R;
import salam.gohajj.custom.activity.LoginActivity;
import salam.gohajj.custom.activity.RequestHandler;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;

public class add_hotel extends AppCompatActivity implements OnClickListener {
    private TextView formatTxt, contentTxt;
    private EditText txtNameHotel, txtAddressHotel,
            editTextuser;
    private Button buttonAddHotel, buttonCancelHotel;
    private Button buttonUpload;
    private CircleImageView imgProfile,imgAgent,imgPemimpin,imgPembimbing;
    private Bitmap bitmapProfile,bitmapAgent,bitmapPemimpin,bitmapPembimbing;
    private RadioGroup rg;
    private static final int PICK_Camera_IMAGE = 2;
    Uri imageUri;
    String created,email,uid,nama;
    private int PICK_IMAGE_REQUEST = 3;
    private Uri filePathProfile,filePathAgent,filePathPemimpin,filePathPembimbing;
    private String selectedProfile,selectedAgent,selectedPemimpin,selectedPembimbing;

    private RatingBar ratingBarHotel ;
    //user
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_hotel);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

//        session = new SessionManager(getApplicationContext());

        //HEADER
        TextView txt_emergency=(TextView) findViewById(R.id.txt_emergency);
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
        txt_thowaf.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), thawaf.class);
                startActivity(i);
            }
        });
        txt_sai.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), sai.class);
                startActivity(i);
            }
        });
        txt_emergency.setOnClickListener(new OnClickListener() {
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

        menu_profile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), profile.class);
                startActivity(i);
            }
        });
        menu_panduan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
            }
        });
        menu_doa.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TitipanDoa.class);
                startActivity(i);
            }
        });
        menu_navigasi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), navigasi.class);
                startActivity(i);
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
            }
        });
        final  ImageView img_setting=(ImageView) findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), setting.class);
                startActivity(i);
            }
        });

        txtNameHotel = (EditText) findViewById(R.id.nama_hotel);
        txtAddressHotel = (EditText) findViewById(R.id.alamat_hotel);
        ratingBarHotel = (RatingBar) findViewById(R.id.hotel_ratingbar);
        editTextuser = (EditText) findViewById(R.id.userid);
        editTextuser.setVisibility(View.GONE);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        nama = user.get("name");
        email = user.get("email");
        created = user.get("created_at");
        //user
//        getData();
        editTextuser.setText(uid);

        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/salam.gohajj.custom/images/"+uid+".png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }

        buttonAddHotel = (Button) findViewById(R.id.buttonSetLokasi);
        buttonCancelHotel = (Button) findViewById(R.id.buttonBatalSet);

        //buttonChoose.setOnClickListener(this);
        buttonAddHotel.setOnClickListener(this);
        buttonCancelHotel.setOnClickListener(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void logoutUser() {
        if (session.isLoggedIn()) {
            session.setLogin(false);

            db.deleteUsers();
        }

        // Launching the login activity
        Intent intent = new Intent(add_hotel.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

        public void onClick(View v){
        if(v == buttonAddHotel){
            if(txtNameHotel.getText().toString().equals(""))
            {
                Toast.makeText(this, getResources().getString(R.string.hotel_tidak_boleh_kosong), Toast.LENGTH_SHORT).show();
            }else if(txtAddressHotel.getText().toString().equals(""))
            {
                Toast.makeText(this, getResources().getString(R.string.alamat_tidak_boleh_kosong), Toast.LENGTH_SHORT).show();
            } else {
                addHotel();
            }
        }if(v == buttonCancelHotel){
                Intent intent = new Intent(getApplicationContext(), navigasi.class);
                startActivity(intent);
        }
    }

    //Adding an addBarcode
    private void addHotel(){
//
        final String idUser = editTextuser.getText().toString().trim();
        final String name = txtNameHotel.getText().toString().trim();
        final String address = txtAddressHotel.getText().toString().trim();
        final String rating=String.valueOf(ratingBarHotel.getRating());

        class AddHotel extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(add_hotel.this,getResources().getString(R.string.menyimpan)+"...","",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject jObj = new JSONObject(s);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONObject hotel = jObj.getJSONObject("data");
                        String id = hotel.getString("id");

                        Intent intent = new Intent(add_hotel.this,
                                MapsActivity.class);
                        intent.putExtra(AppConfig.KEY_ID,id);
                        intent.putExtra(AppConfig.KEY_NAVIGASI,"HOTEL");
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
//                        String errorMsg = jObj.getString("error_msg");
                        JSONObject hotel = jObj.getJSONObject("data");
                        String errorMsg = hotel.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    //e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
//                startActivity(new Intent(add_hotel.this, MapsActivity.class));
            }

            @Override
            protected String doInBackground(Bitmap... params) {

                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_USERID, idUser);
                data.put(AppConfig.KEY_NAME,name);
                data.put(AppConfig.KEY_ADDRESS,address);
                data.put(AppConfig.KEY_RATING,rating);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(AppConfig.URL_ADD_HOTEL, data);
                return res;
            }
        }

        AddHotel ae = new AddHotel();
        ae.execute();

//        startActivity(new Intent(add_hotel.this, navigasi.class));
    }

}
